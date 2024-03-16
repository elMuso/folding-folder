package com.collapser.collapser;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {

    @Nullable
    @Override
    public Object getData(@NotNull Collection<? extends AbstractTreeNode<?>> collection, @NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent, @NotNull Collection<AbstractTreeNode<?>> children, ViewSettings viewSettings) {
        List<AbstractTreeNode<?>> resultList = new ArrayList<>();
        if (parent.getValue() instanceof PsiDirectory directory) {
            String path = directory.getVirtualFile().getPath();
            if (SettingsManager.isComposed(path) || PropertiesComponent.getInstance().getBoolean(SettingConfigurable.PREFIX_GLOBAL, false)) {
                resultList.addAll(createComposedFiles(children, viewSettings));
            } else {
                resultList.addAll(children);
            }
        } else {
            resultList.addAll(children);
        }

        return resultList;
    }

    @NotNull
    private List<AbstractTreeNode<?>> createComposedFiles(@NotNull Collection<AbstractTreeNode<?>> fileNodes, ViewSettings viewSettings) {
        List<AbstractTreeNode<?>> resultList = new ArrayList<>();
        Project project = Utils.getCurrentProject();
        if (project != null) {
            Map<String,ArrayList<AbstractTreeNode<?>>> folders = new LinkedHashMap<>();
            List<AbstractTreeNode<?>> notComposedFileNodes = new ArrayList<>();


            Pattern pattern = Pattern.compile(                    PropertiesComponent.getInstance().getValue(SettingConfigurable.PREFIX_PATTERN, SettingConfigurable.DEFAULT_PATTERN)            );

            for (AbstractTreeNode<?> fileNode : fileNodes) {
                if (fileNode.getValue() instanceof PsiFile psiFile) {
                    String fileName = psiFile.getName();
                        Matcher m = pattern.matcher(fileName);
                        if (m.find()) {
                            String composedDirName = m.group(0);
                            if(!folders.containsKey(composedDirName)){
                                folders.put(composedDirName,new ArrayList<>());
                            }
                            folders.get(composedDirName).add(fileNode);
                        } else {
                            notComposedFileNodes.add(fileNode);
                        }
                }else{
                    notComposedFileNodes.add(fileNode);
                }
            }
            resultList.addAll(notComposedFileNodes);
            List<String> keys = new ArrayList<>(folders.keySet());

            for (int i = 0; i < folders.size(); i++) {
                String name =keys.get(i);
                List<AbstractTreeNode<?>> values = folders.get(name);

                if(values.size()>1){

                    PsiFile psiFile = (PsiFile) values.get(0).getValue();
                    try {
                        psiFile = PsiFileFactory.getInstance(project).createFileFromText(".java", "");
                    } catch (Throwable ignored) {
                    }

                    DirectoryNode composedDirNode = new DirectoryNode(project, viewSettings, psiFile, name);
                    composedDirNode.addAllChildren(values);
                    resultList.add(composedDirNode);

                }else if(values.size()==1){
                    resultList.add(values.get(0));
                }

            }
        }
        return resultList;
    }
}
