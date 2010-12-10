package com.jetbrains.python.debugger;

import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class PyExecutionStack extends XExecutionStack {

  private final PyDebugProcess myDebugProcess;
  private final PyThreadInfo myThreadInfo;
  private PyStackFrame myTopFrame;

  public PyExecutionStack(@NotNull final PyDebugProcess debugProcess, @NotNull final PyThreadInfo threadInfo) {
    super(threadInfo.getName());
    myDebugProcess = debugProcess;
    myThreadInfo = threadInfo;
  }

  @Override
  public XStackFrame getTopFrame() {
    if (myTopFrame == null) {
      final List<PyStackFrameInfo> frames = myThreadInfo.getFrames();
      if (frames != null) {
        myTopFrame = convert(myDebugProcess, frames.get(0));
      }
    }
    return myTopFrame;
  }

  @Override
  public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
    if (myThreadInfo.getState() != PyThreadInfo.State.SUSPENDED) {
      container.errorOccured("Frames not available in non-suspended state");
      return;
    }

    final List<PyStackFrameInfo> frames = myThreadInfo.getFrames();
    if (frames != null && firstFrameIndex <= frames.size()) {
      final List<PyStackFrame> xFrames = new LinkedList<PyStackFrame>();
      for (int i = firstFrameIndex; i < frames.size(); i++) {
        xFrames.add(convert(myDebugProcess, frames.get(i)));
      }
      container.addStackFrames(xFrames, true);
    }
    else {
      container.addStackFrames(Collections.<XStackFrame>emptyList(), true);
    }
  }

  private static PyStackFrame convert(final PyDebugProcess debugProcess, final PyStackFrameInfo frameInfo) {
    return debugProcess.createStackFrame(frameInfo);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PyExecutionStack that = (PyExecutionStack)o;

    if (myThreadInfo != null ? !myThreadInfo.equals(that.myThreadInfo) : that.myThreadInfo != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return myThreadInfo != null ? myThreadInfo.hashCode() : 0;
  }
}
