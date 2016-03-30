package com.jamesward.atomgradleplugin

import spock.lang.Specification

import java.nio.file.attribute.PosixFilePermission

class UnpackSpec extends Specification {

    def 'Unpack.unixModeToPermSet should work'() {
        expect:
        Unpack.unixModeToPermSet(new BigInteger("0100", 8)) == [PosixFilePermission.OWNER_EXECUTE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0200", 8)) == [PosixFilePermission.OWNER_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0300", 8)) == [PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0400", 8)) == [PosixFilePermission.OWNER_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0500", 8)) == [PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0600", 8)) == [PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0700", 8)) == [PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0010", 8)) == [PosixFilePermission.GROUP_EXECUTE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0020", 8)) == [PosixFilePermission.GROUP_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0030", 8)) == [PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0040", 8)) == [PosixFilePermission.GROUP_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0050", 8)) == [PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0060", 8)) == [PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0070", 8)) == [PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0001", 8)) == [PosixFilePermission.OTHERS_EXECUTE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0002", 8)) == [PosixFilePermission.OTHERS_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0003", 8)) == [PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0004", 8)) == [PosixFilePermission.OTHERS_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0005", 8)) == [PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0006", 8)) == [PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0007", 8)) == [PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_READ].toSet()
        Unpack.unixModeToPermSet(new BigInteger("0777", 8)).size() == 9
        Unpack.unixModeToPermSet(new BigInteger("100644", 8)) == [PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ].toSet()
    }

}
