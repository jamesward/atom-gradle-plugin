package com.jamesward.atomgradleplugin

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import spock.lang.Specification

import java.nio.file.Files


class AtomPluginSpec extends Specification {

    def 'AtomPlugin adds the atom task'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.apply(plugin: 'com.jamesward.atomgradleplugin')

        then:
        project.tasks.atom instanceof AtomTask
    }

    def 'extensions exist'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        expect:
        project.extensions.getByName("atom") instanceof AtomPluginExtension
    }

    def 'atom settings works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.atom.filesToOpen = ["foo"]

        then:
        project.atom.filesToOpen == ["foo"]
        project.atom.excludePrereleases == true
        project.atom.version == null
    }

    def 'atom version works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.tasks.atomVersion.execute()

        then:
        project.atom.version == "1.13.0"
    }

    def 'atom home works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.tasks.atomHome.execute()

        then:
        project.atom.version == "1.13.0"
        !project.atom.home.getPath().contains("null")
    }

    def 'atomDownload works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.atom.home = File.createTempDir()
        project.tasks.atomDownload.execute()
        def electronFramework = new File(project.atom.home, 'Atom.app/Contents/Frameworks/Electron Framework.framework/Electron Framework')

        then:
        project.atom.home.exists()
        electronFramework.isFile()
        Files.isSymbolicLink(electronFramework.toPath())
        AtomUtils.apmExec(project.atom.home, project.atom.os).isFile()
        AtomUtils.apmExec(project.atom.home, project.atom.os).canExecute()
        AtomUtils.atomExec(project.atom.home, project.atom.os).isFile()
    }

    def 'atomInstallPackages works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.tasks.atomHome.execute()

        if (!project.atom.home.exists()) {
            project.tasks.atomDownload.execute()
        }
        project.atom.packages = ['heroku-tools']
        project.tasks.atomInstallPackages.execute()

        then:
        new File(System.getProperty("user.home"), ".atom/.apm/heroku-tools").exists()
    }

    def 'atomRun works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.tasks.atomHome.execute()

        if (!project.atom.home.exists()) {
            project.tasks.atomDownload.execute()
        }
        project.tasks.atomRun.execute()

        then:
        notThrown(Exception)
    }

    def 'atom works'() {
        setup:
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: AtomPlugin)

        when:
        project.tasks.atom.execute()

        then:
        notThrown(Exception)
    }

}
