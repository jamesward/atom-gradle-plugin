package com.jamesward.atomgradleplugin

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import org.apache.commons.compress.archivers.zip.ZipFile

class Unpack {

    def static unzip(File file, File destination) {
        def zipFile = new ZipFile(file)

        zipFile.getEntries().collect {
            if (it.isDirectory()) {
                new File(destination, it.getName()).mkdirs()
            }
            else if (it.isUnixSymlink()) {
                def link = new File(destination, it.getName())
                def target = new File(zipFile.getUnixSymlink(it))
                Files.createSymbolicLink(link.toPath(), target.toPath())
            }
            else {
                def zipArchiveEntryInputStream = zipFile.getInputStream(it)
                def newFile = new File(destination, it.getName())
                Files.copy(zipArchiveEntryInputStream, newFile.toPath())
                zipArchiveEntryInputStream.close()
                Files.setPosixFilePermissions(newFile.toPath(), unixModeToPermSet(it.getUnixMode().toBigInteger()))
            }
        }

        zipFile.close()
    }

    def static Set<PosixFilePermission> unixModeToPermSet(BigInteger mode) {
        def allPermissions = [
                (PosixFilePermission.OWNER_READ)    : new BigInteger("0400", 8),
                (PosixFilePermission.OWNER_WRITE)   : new BigInteger("0200", 8),
                (PosixFilePermission.OWNER_EXECUTE) : new BigInteger("0100", 8),
                (PosixFilePermission.GROUP_READ)    : new BigInteger("0040", 8),
                (PosixFilePermission.GROUP_WRITE)   : new BigInteger("0020", 8),
                (PosixFilePermission.GROUP_EXECUTE) : new BigInteger("0010", 8),
                (PosixFilePermission.OTHERS_READ)   : new BigInteger("0004", 8),
                (PosixFilePermission.OTHERS_WRITE)  : new BigInteger("0002", 8),
                (PosixFilePermission.OTHERS_EXECUTE): new BigInteger("0001", 8)
        ]

        def permissions = allPermissions.findAll {
            (mode | it.value) == mode
        }

        permissions.keySet()
    }

}