// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.idea.devkit.inspections.internal

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.RemoveAnnotationQuickFix
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiUtil
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.idea.devkit.DevKitBundle
import org.jetbrains.idea.devkit.inspections.DevKitInspectionBase

class RedundantScheduledForRemovalAnnotationInspection : DevKitInspectionBase() {
  override fun buildInternalVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
    if (!PsiUtil.isLanguageLevel9OrHigher(holder.file)) return PsiElementVisitor.EMPTY_VISITOR
      
    return object : JavaElementVisitor() {
      override fun visitClass(aClass: PsiClass) {
        visitAnnotatedElement(aClass)
      }

      override fun visitMethod(method: PsiMethod) {
        visitAnnotatedElement(method)
      }

      override fun visitField(field: PsiField) {
        visitAnnotatedElement(field)
      }

      private fun visitAnnotatedElement(element: PsiModifierListOwner) {
        val forRemovalAnnotation = element.getAnnotation(ApiStatus.ScheduledForRemoval::class.java.canonicalName) ?: return
        val deprecatedAnnotation = element.getAnnotation(CommonClassNames.JAVA_LANG_DEPRECATED) ?: return
        val forRemovalAttribute = deprecatedAnnotation.findAttribute("forRemoval")?.attributeValue
        val alreadyHasAttribute = (forRemovalAttribute as? JvmAnnotationConstantValue)?.constantValue == true
        if (alreadyHasAttribute) {
          holder.registerProblem(forRemovalAnnotation, DevKitBundle.message("inspection.message.scheduled.for.removal.annotation.can.be.removed"), RemoveAnnotationQuickFix(forRemovalAnnotation, element))
        }
        else {
          val fix = ReplaceAnnotationByForRemovalAttributeFix()
          holder.registerProblem(forRemovalAnnotation, DevKitBundle.message("inspection.message.scheduled.for.removal.annotation.can.be.replaced.by.attribute"), fix)
        }
      }
    }
  }

  class ReplaceAnnotationByForRemovalAttributeFix : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
      val forRemovalAnnotation = descriptor.psiElement as? PsiAnnotation ?: return
      val deprecatedAnnotation = forRemovalAnnotation.owner?.findAnnotation(CommonClassNames.JAVA_LANG_DEPRECATED) ?: return
      val javaFile = forRemovalAnnotation.containingFile as? PsiJavaFile
      forRemovalAnnotation.delete()
      val trueLiteral = JavaPsiFacade.getElementFactory(project).createExpressionFromText(PsiKeyword.TRUE, null)
      deprecatedAnnotation.setDeclaredAttributeValue("forRemoval", trueLiteral)
      if (javaFile != null) {
        JavaCodeStyleManager.getInstance(project).removeRedundantImports(javaFile)
      }
    }

    override fun getName(): String = familyName

    override fun getFamilyName(): String {
      return DevKitBundle.message("inspection.fix.name.remove.scheduled.for.removal.annotation.by.attribute")
    }
  }
}