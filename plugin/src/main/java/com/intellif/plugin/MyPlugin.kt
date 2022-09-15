package com.intellif.plugin

import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("MyPlugin==>apply")
        val androidComponentsExtension =
            project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponentsExtension.onVariants {
            it.instrumentation.transformClassesWith(
                LogClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {}
        }
    }
}