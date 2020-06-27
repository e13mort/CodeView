/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

import di.DaggerCliComponent
import factory.LaunchCommand
import java.nio.file.FileSystems
import java.nio.file.Files

const val SYSTEM_PROPERTY_HOME = "user.home"
const val WORKING_DIR_NAME = "cv_cache"

private val workingDirectory by lazy {
    FileSystems.getDefault().getPath(System.getProperty(SYSTEM_PROPERTY_HOME)).resolve(WORKING_DIR_NAME)
}

fun main(params: Array<String>) {
    val launchCommand = LaunchCommand()
    launchCommand.main(params)

    if (!Files.exists(workingDirectory)) Files.createDirectory(workingDirectory)

    DaggerCliComponent.builder()
        .launchCommand(launchCommand)
        .root(workingDirectory)
        .build()
        .codeView()
        .run(launchCommand.sourcesPath)
        .subscribe(::printResult, ::println)
}

private fun printResult(res: String) {
    println(res)
}
