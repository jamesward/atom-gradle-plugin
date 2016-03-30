package com.jamesward.atomgradleplugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class AtomPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('atom', AtomPluginExtension)
        project.task('atomDownload', type: AtomDownloadTask)
        project.task('atomInstallPackages', type: AtomInstallPackagesTask)
        project.task('atomRun', type: AtomRunTask)
        project.task('atom', type: AtomTask)
    }

}

class AtomPluginExtension {
    def Boolean excludePrereleases = true
    def String version = AtomUtils.latest(excludePrereleases).name
    def OS os = OS.ofName(System.getProperty("os.name"))
    def String[] packages = []
    def String[] filesToOpen = ["."]
    def File home = new File(System.getProperty('user.home'), '.atom/' + version) // todo: windows
}

class AtomDownloadTask extends DefaultTask {

    def unzipAsset(String name) {
        def asset = AtomUtils.release(project.atom.version).assets.find { it.name == name }
        def tmpFile = File.createTempFile(asset.name, '.zip')
        project.ant.get(src: asset.downloadUrl, dest: tmpFile)
        // home-grown unzip that supports symlinks
        Unpack.unzip(tmpFile, project.atom.home)
    }

    @TaskAction
    def atomDownload() {
        switch (project.atom.os) {
            case OS.Windows:
                unzipAsset('atom-windows.zip')
                break
            case OS.Mac:
                unzipAsset('atom-mac.zip')
                break
            default:
                break
        }
    }

}

class AtomInstallPackagesTask extends DefaultTask {

    @TaskAction
    def atomInstallPackages() {
        def apm = AtomUtils.apmExec(project.atom.home, project.atom.os)

        def stdout = new ByteArrayOutputStream()

        project.exec {
            commandLine(apm.getAbsolutePath(), 'list')
            standardOutput(stdout)
        }

        def apmList = stdout.toString().readLines()

        project.atom.packages.each {
            String packageName = it
            if (apmList.any { it.contains(packageName) }) {
                project.exec {
                    commandLine(apm.getAbsolutePath(), 'upgrade', '-c', 'false', packageName)
                    standardOutput(new ByteArrayOutputStream())
                }
            }
            else {
                project.exec {
                    commandLine(apm.getAbsolutePath(), 'install', '-s', packageName)
                    standardOutput(new ByteArrayOutputStream())
                }
            }
        }
    }

}

class AtomRunTask extends DefaultTask {

    @TaskAction
    def atomRun() {
        def atom = AtomUtils.atomExec(project.atom.home, project.atom.os)

        def stdout = new ByteArrayOutputStream()
        def stderr = new ByteArrayOutputStream()

        project.exec {
            commandLine(atom.getAbsolutePath())
            args(project.atom.filesToOpen)
            environment(['ATOM_PATH': project.atom.home.getAbsolutePath()])
            workingDir(project.projectDir)
            standardOutput(stdout)
            errorOutput(stderr)
        }

        if (stderr.toString().length() > 0) {
            throw new Exception(stdout.toString())
        }
    }

}

class AtomTask extends DefaultTask {

    @TaskAction
    def atom() {

        if (!project.atom.home.exists()) {
            project.tasks.atomDownload.execute()
        }

        project.tasks.atomInstallPackages.execute()
        project.tasks.atomRun.execute()
    }

}