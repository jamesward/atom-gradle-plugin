package com.jamesward.atomgradleplugin

import spock.lang.Specification

class AtomUtilsSpec extends Specification {

    def 'AtomUtils.releases should get the releases'() {
        when:
        Release[] releases = AtomUtils.releases()

        then:
        releases.length > 0
    }

    def 'AtomUtils.latest should get the latest release'() {
        when:
        Release latestRelease = AtomUtils.latest()
        Release latestPrerelease = AtomUtils.latest(false)

        then:
        latestRelease.name == '1.13.0'
        latestRelease.assets.length > 0
        !latestRelease.prerelease

        latestPrerelease.name == '1.14.0-beta2'
        latestPrerelease.assets.length > 0
        latestPrerelease.prerelease
    }

}
