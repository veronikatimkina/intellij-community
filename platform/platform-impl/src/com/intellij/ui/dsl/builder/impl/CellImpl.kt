// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ui.dsl.builder.impl

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.util.whenTextChanged
import com.intellij.openapi.ui.DialogValidation
import com.intellij.openapi.ui.DialogValidationRequestor
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.Label
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.HyperlinkEventAction
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.gridLayout.Gaps
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.ui.layout.*
import com.intellij.util.SmartList
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nls
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.text.JTextComponent

@ApiStatus.Internal
internal class CellImpl<T : JComponent>(
  private val dialogPanelConfig: DialogPanelConfig,
  component: T,
  private val parent: RowImpl,
  val viewComponent: JComponent = component) : CellBaseImpl<Cell<T>>(), Cell<T> {

  override var component: T = component
    private set

  override var comment: JEditorPane? = null
    private set

  var label: JLabel? = null
    private set

  var labelPosition: LabelPosition = LabelPosition.LEFT
    private set

  var widthGroup: String? = null
    private set

  private var property: ObservableProperty<*>? = null
  private var applyIfEnabled = false

  private var visible = viewComponent.isVisible
  private var enabled = viewComponent.isEnabled

  override fun horizontalAlign(horizontalAlign: HorizontalAlign): CellImpl<T> {
    super.horizontalAlign(horizontalAlign)
    return this
  }

  override fun verticalAlign(verticalAlign: VerticalAlign): CellImpl<T> {
    super.verticalAlign(verticalAlign)
    return this
  }

  override fun resizableColumn(): CellImpl<T> {
    super.resizableColumn()
    return this
  }

  override fun gap(rightGap: RightGap): CellImpl<T> {
    super.gap(rightGap)
    return this
  }

  override fun focused(): CellImpl<T> {
    dialogPanelConfig.preferredFocusedComponent = component
    return this
  }

  override fun applyToComponent(task: T.() -> Unit): CellImpl<T> {
    component.task()
    return this
  }

  override fun enabledFromParent(parentEnabled: Boolean) {
    doEnabled(parentEnabled && enabled)
  }

  override fun enabled(isEnabled: Boolean): CellImpl<T> {
    enabled = isEnabled
    if (parent.isEnabled()) {
      doEnabled(enabled)
    }
    return this
  }

  override fun enabledIf(predicate: ComponentPredicate): Cell<T> {
    super.enabledIf(predicate)
    return this
  }

  override fun visibleFromParent(parentVisible: Boolean) {
    doVisible(parentVisible && visible)
  }

  override fun visible(isVisible: Boolean): CellImpl<T> {
    visible = isVisible
    if (parent.isVisible()) {
      doVisible(visible)
    }
    return this
  }

  override fun visibleIf(predicate: ComponentPredicate): CellImpl<T> {
    super.visibleIf(predicate)
    return this
  }

  override fun bold(): CellImpl<T> {
    component.font = component.font.deriveFont(Font.BOLD)
    return this
  }

  override fun comment(@NlsContexts.DetailedDescription comment: String?, maxLineLength: Int, action: HyperlinkEventAction): CellImpl<T> {
    this.comment = if (comment == null) null else createComment(comment, maxLineLength, action)
    return this
  }

  override fun label(label: String, position: LabelPosition): CellImpl<T> {
    return label(Label(label), position)
  }

  override fun label(label: JLabel, position: LabelPosition): CellImpl<T> {
    this.label = label
    labelPosition = position
    label.putClientProperty(DslComponentPropertyInternal.CELL_LABEL, true)
    return this
  }

  override fun widthGroup(group: String): CellImpl<T> {
    widthGroup = group
    return this
  }

  override fun applyIfEnabled(): CellImpl<T> {
    applyIfEnabled = true
    return this
  }

  override fun accessibleName(name: String): CellImpl<T> {
    component.accessibleContext.accessibleName = name
    return this
  }

  override fun accessibleDescription(description: String): CellImpl<T> {
    component.accessibleContext.accessibleDescription = description
    return this
  }

  override fun <V> bind(componentGet: (T) -> V, componentSet: (T, V) -> Unit, binding: PropertyBinding<V>): CellImpl<T> {
    componentSet(component, binding.get())

    onApply { if (shouldSaveOnApply()) binding.set(componentGet(component)) }
    onReset { componentSet(component, binding.get()) }
    onIsModified { shouldSaveOnApply() && componentGet(component) != binding.get() }
    return this
  }

  override fun validationRequestor(validationRequestor: (() -> Unit) -> Unit): CellImpl<T> {
    return validationRequestor(DialogValidationRequestor.create(validationRequestor))
  }

  override fun validationRequestor(validationRequestor: DialogValidationRequestor): CellImpl<T> {
    val origin = component.origin
    dialogPanelConfig.componentValidationRequestors.getOrPut(origin) { SmartList() }
      .add(validationRequestor)
    return this
  }

  override fun validationOnApply(validation: ValidationInfoBuilder.(T) -> ValidationInfo?): CellImpl<T> {
    val origin = component.origin
    return validationOnApply(DialogValidation.create { ValidationInfoBuilder(origin).validation(component) })
  }

  override fun validationOnApply(validation: DialogValidation): CellImpl<T> {
    val origin = component.origin
    dialogPanelConfig.componentValidationsOnApply.getOrPut(origin) { SmartList() }
      .add(validation)
    return this
  }

  override fun errorOnApply(message: String, condition: (T) -> Boolean): CellImpl<T> {
    return validationOnApply { if (condition(it)) error(message) else null }
  }

  override fun validationOnInput(validation: ValidationInfoBuilder.(T) -> ValidationInfo?): CellImpl<T> {
    val origin = component.origin
    return validationOnInput(DialogValidation.create { ValidationInfoBuilder(origin).validation(component) })
  }

  override fun validationOnInput(validation: DialogValidation): CellImpl<T> {
    val origin = component.origin
    dialogPanelConfig.componentValidations.getOrPut(origin) { SmartList() }
      .add(validation)

    // Fallback in case if no validation requestors is defined
    guessAndInstallValidationRequestor()

    return this
  }

  private fun guessAndInstallValidationRequestor() {
    val stackTrace = Throwable()
    val origin = component.origin
    val componentValidationRequestors = dialogPanelConfig.componentValidationRequestors.getOrPut(origin) { SmartList() }
    componentValidationRequestors.add(object : DialogValidationRequestor {
      override fun subscribe(parentDisposable: Disposable?, validate: () -> Unit) {
        if (dialogPanelConfig.panelValidationRequestors.isNotEmpty()) return
        if (componentValidationRequestors.size > 1) return

        logger<Cell<*>>().warn("Please, install Cell.validationRequestor or Panel.validationRequestor", stackTrace)
        when (val property = property) {
          null -> when (origin) {
            is JTextComponent -> origin.whenTextChanged(parentDisposable) { validate() }
          }
          is GraphProperty -> when (parentDisposable) {
            null -> property.afterPropagation { validate() }
            else -> property.afterPropagation(parentDisposable) { validate() }
          }
          else -> when (parentDisposable) {
            null -> property.afterChange { validate() }
            else -> property.afterChange({ validate() }, parentDisposable)
          }
        }
      }
    })
  }

  override fun onApply(callback: () -> Unit): CellImpl<T> {
    dialogPanelConfig.applyCallbacks.register(component, callback)
    return this
  }

  override fun onReset(callback: () -> Unit): CellImpl<T> {
    dialogPanelConfig.resetCallbacks.register(component, callback)
    return this
  }

  override fun onIsModified(callback: () -> Boolean): CellImpl<T> {
    dialogPanelConfig.isModifiedCallbacks.register(component, callback)
    return this
  }

  override fun customize(customGaps: Gaps): CellImpl<T> {
    super.customize(customGaps)
    return this
  }

  private fun shouldSaveOnApply(): Boolean {
    return !(applyIfEnabled && !viewComponent.isEnabled)
  }

  private fun doVisible(isVisible: Boolean) {
    if (viewComponent.isVisible != isVisible) {
      viewComponent.isVisible = isVisible
      comment?.let { it.isVisible = isVisible }
      label?.let { it.isVisible = isVisible }

      // Force parent to re-layout
      viewComponent.parent?.revalidate()
    }
  }

  private fun doEnabled(isEnabled: Boolean) {
    viewComponent.isEnabled = isEnabled
    comment?.let { it.isEnabled = isEnabled }
    label?.let { it.isEnabled = isEnabled }
  }

  companion object {
    internal fun Cell<*>.installValidationRequestor(property: ObservableProperty<*>) {
      if (this is CellImpl) {
        this.property = property
      }
    }
  }
}

private const val HTML = "<html>"

@Deprecated("Not needed in the future")
@ApiStatus.ScheduledForRemoval
internal fun removeHtml(text: @Nls String): @Nls String {
  return if (text.startsWith(HTML, ignoreCase = true)) text.substring(HTML.length) else text
}
