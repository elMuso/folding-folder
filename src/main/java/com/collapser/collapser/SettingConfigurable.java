package com.collapser.collapser;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * Created by skyrylyuk on 10/15/15.
 */
public class SettingConfigurable implements Configurable {
    public static final String PREFIX_PATTERN = "folding_plugin_prefix_pattern";
    public static final String PREFIX_HIDE = "folding_plugin_prefix_hide";
    public static final String PREFIX_GLOBAL = "folding_plugin_global_mode";

    public static final String DEFAULT_PATTERN = "^.[A-Za-z0-9]*?(?=_|([A-Z]|\\.))";
    public static final String DEFAULT_PATTERN_DOUBLE = "^.[A-Za-z0-9]*?(?=_|([A-Z]|\\.))";

    private JPanel mPanel;
    private JTextField customPattern;
    private JCheckBox hideFoldingPrefix;
    private JCheckBox globalMode;
    private boolean isModified = false;

    @Nls
    @Override
    public String getDisplayName() {
        return "Android Folding";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "null:";
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        customPattern.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isModified = true;
            }
        });

        hideFoldingPrefix.addActionListener(actionEvent -> isModified = true);
        globalMode.addActionListener(actionEvent -> isModified = true);
        reset();

        return mPanel;
    }


    @Override
    public boolean isModified() {

        return isModified;
    }

    @Override
    public void apply() {
        PropertiesComponent.getInstance().setValue(PREFIX_PATTERN, customPattern.getText());
        PropertiesComponent.getInstance().setValue(PREFIX_HIDE, Boolean.valueOf(hideFoldingPrefix.isSelected()).toString());
        PropertiesComponent.getInstance().setValue(PREFIX_GLOBAL, Boolean.valueOf(globalMode.isSelected()).toString());

        if (isModified) {
            Project currentProject = Utils.getCurrentProject();

            if (currentProject != null) {
                ProjectView.getInstance(currentProject).refresh();
            }
        }

        isModified = false;
    }

    @Override
    public void reset() {
        customPattern.setEnabled(true);
        customPattern.setText(PropertiesComponent.getInstance().getValue(PREFIX_PATTERN, DEFAULT_PATTERN_DOUBLE));
        hideFoldingPrefix.getModel().setSelected(PropertiesComponent.getInstance().getBoolean(PREFIX_HIDE, false));
        globalMode.getModel().setSelected(PropertiesComponent.getInstance().getBoolean(PREFIX_GLOBAL, false));
    }

    @Override
    public void disposeUIResources() {
    }
}
