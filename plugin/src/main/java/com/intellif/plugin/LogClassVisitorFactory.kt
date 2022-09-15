package com.intellif.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class LogClassVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return LogClassNode(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        println("isInstrumentable 类名: ${classData.className}")

        return classData.className == "android.util.Log"
    }
}