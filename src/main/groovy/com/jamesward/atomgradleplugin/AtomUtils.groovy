package com.jamesward.atomgradleplugin

import groovy.json.JsonSlurper


class AtomUtils {

    static def Release[] releases() {
        def json = new JsonSlurper().parseText(new URL('https://api.github.com/repos/atom/atom/releases').text)
        json.collect {
            def assets = it.assets.collect {
                new Asset(name: it.name, downloadUrl: it.browser_download_url)
            }
            new Release(name: it.name, prerelease: it.prerelease, assets: assets)
        }
    }

    static def Release release(String version) {
        releases().find { it.name == version }
    }

    static def Release latest(Boolean excludePrereleases = true) {
        def releases = releases().findAll {
            if (excludePrereleases) {
                !it.prerelease
            }
            else {
                true
            }
        }
        releases.head()
    }

    static def File atomExec(File atomHome, OS os) {
        def result
        switch (os) {
            case OS.Windows:
                result = new File(atomHome, 'Atom/atom.exe')
                break
            case OS.Mac:
                result = new File(atomHome, 'Atom.app/Contents/Resources/app/atom.sh')
                break
            default:
                result = null
                break
        }

        result
    }

    static def File apmExec(File atomHome, OS os) {
        def result
        switch (os) {
            case OS.Windows:
                result = new File(atomHome, 'Atom/resources/app/apm/bin/apm.cmd')
                break
            case OS.Mac:
                result = new File(atomHome, 'Atom.app/Contents/Resources/app/apm/bin/apm')
                break
            default:
                result = null
                break
        }

        result
    }

}

enum OS {
    Mac('Mac OS X'),
    Windows('Windows'),
    Linux('Linux')

    final String name

    private OS(String name) {
        this.name = name
    }

    static OS ofName(String name) {
        values().find { it.name == name }
    }
}

class Asset {
    String name
    String downloadUrl
}

class Release {
    String name
    Boolean prerelease
    Asset[] assets
}