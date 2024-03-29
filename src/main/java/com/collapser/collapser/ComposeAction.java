package com.collapser.collapser;


import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class ComposeAction extends AnAction {

    private static final String DECOMPOSE = "Ungroup";
    private static final String COMPOSE = "Group";

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Object nav = actionEvent.getData(CommonDataKeys.NAVIGATABLE);
        if (nav instanceof PsiDirectory directory) {

            String path = directory.getVirtualFile().getPath();

            if (SettingsManager.isComposed(path)) {
                SettingsManager.removeComposedFolder(path);
            } else {
                SettingsManager.addComposedFolder(path);
            }

            Project project = actionEvent.getData(CommonDataKeys.PROJECT);
            if (project != null) {
                ProjectView.getInstance(project).refresh();
            }
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent actionEvent) {
        boolean enabledAndVisible = false;
        Project project = actionEvent.getData(CommonDataKeys.PROJECT);
        if (project != null) {
            Object nav = actionEvent.getData(CommonDataKeys.NAVIGATABLE);

            if (nav instanceof PsiDirectory directory) {

                String path = directory.getVirtualFile().getPath();

                if (SettingsManager.isComposed(path)) {
                    actionEvent.getPresentation().setText(DECOMPOSE);
                } else {
                    actionEvent.getPresentation().setText(COMPOSE);
                }

                enabledAndVisible = true;
            }
        }
        actionEvent.getPresentation().setEnabledAndVisible(enabledAndVisible);
    }
}
