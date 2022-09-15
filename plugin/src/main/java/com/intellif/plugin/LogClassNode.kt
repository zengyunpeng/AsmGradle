package com.intellif.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class LogClassNode(private val nextVisitor: ClassVisitor) : ClassNode(Opcodes.ASM5) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("LogClassNode==>visitMethod ($access, $name, $descriptor, $signature, $exceptions)")
//        if (name == "i" && signature == "(String,String)V") {
//
//        }

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

}