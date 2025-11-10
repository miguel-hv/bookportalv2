package com.bookportal.backend.tools;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.google.gson.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class ProjectStructureToJson {
    public static void main(String[] args) throws Exception {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);

        File srcDir = new File("src/main/java");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Map<String, Object>> files = new ArrayList<>();
        processDir(srcDir, files);

        String json = gson.toJson(files);

        String currentDir = new File(ProjectStructureToJson.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI())
                .getPath();

        File outputFile = new File(currentDir, "project_structure.json");

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
            System.out.println("✅ JSON file generated at: " + outputFile.getAbsolutePath());
        }
    }


    static void processDir(File dir, List<Map<String, Object>> list) throws Exception {
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) processDir(f, list);
            else if (f.getName().endsWith(".java")) {
                CompilationUnit cu = StaticJavaParser.parse(f);
                Map<String, Object> fileInfo = new LinkedHashMap<>();
                fileInfo.put("file", f.getPath());

                cu.getPackageDeclaration().ifPresent(p -> fileInfo.put("package", p.getNameAsString()));

                List<Map<String, Object>> classes = new ArrayList<>();
                for (TypeDeclaration<?> type : cu.getTypes()) {
                    Map<String, Object> cls = new LinkedHashMap<>();
                    cls.put("name", type.getNameAsString());
                    List<String> methods = new ArrayList<>();
                    for (MethodDeclaration m : type.getMethods()) {
                        methods.add(m.getDeclarationAsString(false, false, false));
                    }
                    cls.put("methods", methods);
                    classes.add(cls);
                }
                fileInfo.put("classes", classes);
                list.add(fileInfo);
            }
        }
    }
}

