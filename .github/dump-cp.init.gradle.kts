allprojects {
    tasks.register("dumpCp") {
        doLast {
            val out = rootProject.file("deps-out/${project.name}")
            out.mkdirs()
            listOf("compileClasspath", "clientCompileClasspath", "annotationProcessor").forEach { cfgName ->
                val cfg = project.configurations.findByName(cfgName) ?: return@forEach
                if (!cfg.isCanBeResolved) return@forEach
                val dir = File(out, cfgName); dir.mkdirs()
                cfg.resolve().forEach { f ->
                    if (f.isFile) f.copyTo(File(dir, f.name), overwrite = true)
                    else if (f.isDirectory) project.copy { from(f); into(File(dir, f.name + ".dir")) }
                }
            }
        }
    }
}
