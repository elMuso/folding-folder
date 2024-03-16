package com.collapser.collapser;


import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

class FoldingNode extends PsiFileNode {
    private final String mName;

    public FoldingNode(Project project, PsiFile value, ViewSettings viewSettings, String shortName) {
        super(project, value, viewSettings);
        mName = shortName;
    }

    @Override
    protected void updateImpl(@NotNull PresentationData presentationData) {
        super.updateImpl(presentationData);

        presentationData.setPresentableText(mName);
    }
}
