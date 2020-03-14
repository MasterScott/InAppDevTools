/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.tasks

import es.rafaco.inappdevtools.InAppDevToolsPlugin
import es.rafaco.inappdevtools.utils.ProjectUtils
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.diagnostics.DependencyReportTask


class DependencyTask extends DependencyReportTask {

    ProjectUtils projectUtils

    public DependencyTask() {
        super()
        this.description = 'Generated dependencies report if needed'
        this.group = InAppDevToolsPlugin.TAG
        this.projectUtils = new ProjectUtils(getProject())
    }

    @InputFile
    File inputFile = project.file(getProject().buildscript.sourceFile)

    @OutputFile
    File outputFile = InAppDevToolsPlugin.getOutputFile(getProject(),
            'gradle_dependencies.txt')

    @Override
    @TaskAction
    public void generate() {
        boolean isDebug = InAppDevToolsPlugin.isDebug(getProject())

        if (isDebug) println "OutputFile: ${outputFile}"
        if (isDebug) println "Input: ${inputFile}"

        if (projectUtils.isAndroidApplication()){
            def variantName = projectUtils.getCurrentBuildVariant().uncapitalize()
            def targetConfiguration = variantName + 'Runtime' + 'Classpath'
            setConfiguration(targetConfiguration)
        }else{
            setConfiguration('default')
        }
        if (isDebug) println "Configurations: ${getConfigurations()}"

        // Manual skip needed because AbstractReportTask constructor have Task.upToDateWhen{false}
        if (outputFile.lastModified() >inputFile.lastModified()){
            if (isDebug) print 'Skipped manually: UP-TO-DATE'
            return
        }

        super.generate();
        println "Generated dependency report into ${outputFile}"
    }
}
