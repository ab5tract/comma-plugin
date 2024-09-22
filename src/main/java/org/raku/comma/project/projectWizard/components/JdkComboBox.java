// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.comma.project.projectWizard.components;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.roots.ui.configuration.SdkListItem.SdkItem;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.ComboBoxPopupState;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * @author Eugene Zhuravlev
 */
public class JdkComboBox extends SdkComboBoxBase<JdkComboBox.JdkComboBoxItem> {
  private static final Logger LOG = Logger.getInstance(JdkComboBox.class);
  @NotNull private final Consumer<Sdk> myOnNewSdkAdded;

  @Nullable private JButton mySetUpButton;

  /**
   * Creates new Sdk selector combobox
   * @param project current project (if any)
   * @param sdkModel the sdks model
   * @param sdkTypeFilter sdk types filter predicate to show
   * @param sdkFilter filters Sdk instances that are listed, it implicitly includes the {@param sdkTypeFilter}
   * @param creationFilter a filter of SdkType that allowed to create a new Sdk with that control
   * @param onNewSdkAdded a callback that is executed once a new Sdk is added to the list
   */
  public JdkComboBox(@Nullable Project project,
                     @NotNull ProjectSdksModel sdkModel,
                     @Nullable Condition<? super SdkTypeId> sdkTypeFilter,
                     @Nullable Condition<? super Sdk> sdkFilter,
                     @Nullable Condition<? super SdkTypeId> creationFilter,
                     @Nullable Consumer<? super Sdk> onNewSdkAdded) {
    super(new SdkListModelBuilder(project, sdkModel, sdkTypeFilter, creationFilter, sdkFilter));
    myOnNewSdkAdded = sdk -> {
      if (onNewSdkAdded != null) {
        onNewSdkAdded.consume(sdk);
      }
    };
    reloadModel();
  }

  @Override
  protected void onModelUpdated(@NotNull SdkListModel model) {
    Object previousSelection = getSelectedItem();
    JdkComboBoxModel newModel = new JdkComboBoxModel(model);
    newModel.setSelectedItem(previousSelection);
    setModel(newModel);
  }

  @NotNull
  private static JdkComboBoxItem wrapItem(@NotNull SdkListItem item) {
      return switch (item) {
          case SdkListItem.NoneSdkItem noneSdkItem -> new NoneJdkComboBoxItem();
          case SdkListItem.ProjectSdkItem projectSdkItem -> new ProjectJdkComboBoxItem();
          case SdkListItem.ActionItem actionItem -> new ActionJdkComboBoxItem(actionItem);
          case SdkListItem.SdkReferenceItem referenceItem -> new ReferenceJdkComboBoxItem(referenceItem);
          case SdkListItem.SuggestedItem suggestedItem -> new SuggestedJdkComboBoxItem(suggestedItem);
          case SdkItem sdkItem -> new ActualJdkInnerItem(sdkItem);
          default -> new InnerJdkComboBoxItem(item);
      };
  }

  /**
   * @deprecated Use the overloaded constructor to pass these parameters directly to
   * that class. The {@param setUpButton} is no longer used, the JdkComboBox shows
   * all the needed actions in the popup. The button will be made invisible.
   */
  @Deprecated
  @SuppressWarnings("unused")
  public void setSetupButton(final JButton setUpButton,
                                @Nullable final Project project,
                                final ProjectSdksModel jdksModel,
                                final JdkComboBoxItem firstItem,
                                @Nullable final Condition<? super Sdk> additionalSetup,
                                final boolean moduleJdkSetup) {
    setSetupButton(setUpButton, project, jdksModel, firstItem, additionalSetup,"");
  }

  /**
   * @deprecated Use the overloaded constructor to pass these parameters directly to
   * that class. The {@param setUpButton} is no longer used, the JdkComboBox shows
   * all the needed actions in the popup. The button will be made invisible.
   */
  @Deprecated
  @SuppressWarnings("unused")
  public void setSetupButton(final JButton setUpButton,
                                @Nullable final Project project,
                                final ProjectSdksModel jdksModel,
                                final JdkComboBoxItem firstItem,
                                @Nullable final Condition<? super Sdk> additionalSetup,
                                final String actionGroupTitle) {

    mySetUpButton = setUpButton;
    mySetUpButton.setVisible(false);
  }

  /**
   *
   * @deprecated the popup shown by the SetUp button is now included
   * directly into the popup, you may remove the button from your UI,
   * see {@link #setSetupButton(JButton, Project, ProjectSdksModel, JdkComboBoxItem, Condition, boolean)}
   * for more details
   */
  @Nullable
  @Deprecated
  public JButton getSetUpButton() {
    return mySetUpButton;
  }

  @Nullable
  @Override
  public JdkComboBoxItem getSelectedItem() {
    return (JdkComboBoxItem) super.getSelectedItem();
  }

  @Nullable
  public Sdk getSelectedJdk() {
    final JdkComboBoxItem selectedItem = getSelectedItem();
    return selectedItem != null? selectedItem.getJdk() : null;
  }

  public void setSelectedJdk(@Nullable Sdk jdk) {
    setSelectedItem(jdk);
  }

  /**
   * @deprecated use {@link #reloadModel()}, you may also need to call
   * {@link #showNoneSdkItem()} or {@link #showProjectSdkItem()} once
   */
  @Deprecated
  @SuppressWarnings("unused")
  public void reloadModel(JdkComboBoxItem firstItem, @Nullable Project project) {
    processFirstItem(firstItem);
    reloadModel();
  }

  private void processFirstItem(@Nullable JdkComboBoxItem firstItem) {
    if (firstItem instanceof ProjectJdkComboBoxItem) {
      myModel.showProjectSdkItem();
    } else if (firstItem instanceof NoneJdkComboBoxItem) {
      myModel.showNoneSdkItem();
    } else if (firstItem instanceof ActualJdkComboBoxItem) {
      setSelectedJdk(((ActualJdkComboBoxItem)firstItem).myJdk);
    }
  }

  @Override
  public void firePopupMenuWillBecomeVisible() {
    resolveSuggestionsIfNeeded();
    super.firePopupMenuWillBecomeVisible();
  }

  private void resolveSuggestionsIfNeeded() {
    myModel.reloadActions();

    DialogWrapper dialogWrapper = DialogWrapper.findInstance(this);
    if (dialogWrapper == null) {
      LOG.warn("Cannot find DialogWrapper parent for the JdkComboBox " + this + ", SDK search is disabled", new RuntimeException());
      return;
    }

    myModel.detectItems(this, dialogWrapper.getDisposable());
  }

  @Override
  public void setSelectedItem(@Nullable Object anObject) {
    if (anObject instanceof SdkListItem) {
      setSelectedItem(wrapItem((SdkListItem)anObject));
      return;
    }

    if (anObject == null) {
      SdkListModel innerModel = ((JdkComboBoxModel) getModel()).myInnerModel;
      SdkListItem candidate = innerModel.findProjectSdkItem();
      if (candidate == null) {
        candidate = innerModel.findNoneSdkItem();
      }
      if (candidate == null) {
        candidate = myModel.showProjectSdkItem();
      }

      setSelectedItem(candidate);
      return;
    }

    if (anObject instanceof Sdk) {
      // it is a chance we have a cloned SDK instance from the model here, or an original one
      // reload model is needed to make sure we see all instances
      myModel.reloadSdks();
      ((JdkComboBoxModel) getModel()).trySelectSdk((Sdk)anObject);
      return;
    }

    if (anObject instanceof InnerComboBoxItem) {
      SdkListItem item = ((InnerComboBoxItem) anObject).getItem();
      if (myModel.executeAction(this, item, newItem -> {
        setSelectedItem(newItem);
        if (newItem instanceof SdkItem) {
          myOnNewSdkAdded.consume(((SdkItem) newItem).sdk);
        }
      })) return;
    }

    if (anObject instanceof SelectableComboBoxItem) {
      super.setSelectedItem(anObject);
    }
  }

  private static class JdkComboBoxModel extends AbstractListModel<JdkComboBoxItem>
                                        implements ComboBoxPopupState<JdkComboBoxItem>,
                                                   ComboBoxModel<JdkComboBoxItem>
  {
    private final SdkListModel myInnerModel;
    private JdkComboBoxItem mySelectedItem;

    JdkComboBoxModel(@NotNull SdkListModel innerModel) {
      myInnerModel = innerModel;
    }

    @Override
    public int getSize() {
      return myInnerModel.getItems().size();
    }

    @Override
    public JdkComboBoxItem getElementAt(int index) {
      return wrapItem(myInnerModel.getItems().get(index));
    }

    @Nullable
    @Override
    public ListModel<JdkComboBoxItem> onChosen(JdkComboBoxItem selectedValue) {
      if (selectedValue instanceof InnerComboBoxItem) {
        SdkListModel inner = myInnerModel.onChosen(((InnerComboBoxItem)selectedValue).getItem());
        return inner == null ? null : new JdkComboBoxModel(inner);
      }
      return null;
    }

    @Override
    public boolean hasSubstep(JdkComboBoxItem selectedValue) {
      if (selectedValue instanceof InnerComboBoxItem) {
        return myInnerModel.hasSubstep(((InnerComboBoxItem)selectedValue).getItem());
      }
      return false;
    }

    @Override
    public void setSelectedItem(Object anObject) {
      if (!(anObject instanceof JdkComboBoxItem)) return;
      if (!(anObject instanceof InnerComboBoxItem)) return;
      SdkListItem innerItem = ((InnerComboBoxItem)anObject).getItem();
      if (!myInnerModel.getItems().contains(innerItem)) return;
      mySelectedItem = (JdkComboBoxItem)anObject;
      fireContentsChanged(this, -1, -1);
    }

    @Override
    public Object getSelectedItem() {
      return mySelectedItem;
    }

    void trySelectSdk(@NotNull Sdk sdk) {
      SdkItem item = myInnerModel.findSdkItem(sdk);
      if (item == null) return;
      setSelectedItem(wrapItem(item));
    }
  }

  @NotNull
  public static Condition<Sdk> getSdkFilter(@Nullable final Condition<? super SdkTypeId> filter) {
    return filter == null ? Conditions.alwaysTrue() : sdk -> filter.value(sdk.getSdkType());
  }

  private interface InnerComboBoxItem {
    @NotNull SdkListItem getItem();
  }

  private interface SelectableComboBoxItem { }

  public abstract static class JdkComboBoxItem {
    @Nullable public Sdk getJdk() {
      return null;
    }
    @Nullable public String getSdkName() {
      return null;
    }
  }

  private static class InnerJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem {
    private final SdkListItem myItem;
    private InnerJdkComboBoxItem(@NotNull SdkListItem item) { myItem = item; }
    @NotNull @Override public SdkListItem getItem() {
      return myItem;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InnerJdkComboBoxItem item = (InnerJdkComboBoxItem)o;
      return myItem.equals(item.myItem);
    }

    @Override public @NotNull String toString() { return myItem.getClass().toString(); }
    @Override public int hashCode() {
      return Objects.hash(myItem);
    }
  }

  private static class ActualJdkInnerItem extends ActualJdkComboBoxItem implements InnerComboBoxItem {
    private final SdkItem myItem;

    private ActualJdkInnerItem(@NotNull SdkItem item) {
      super(item.sdk);
      myItem = item;
    }

    @NotNull @Override public SdkListItem getItem() {
      return myItem;
    }
  }

  public static class ReferenceJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem {
    private final SdkListItem.SdkReferenceItem myItem;
    public ReferenceJdkComboBoxItem(SdkListItem.SdkReferenceItem item) { myItem = item; }
    @Override public @NotNull SdkListItem getItem() { return myItem; }
    @Override public @NotNull String toString() { return myItem.name; }
  }

  public static class ActualJdkComboBoxItem extends JdkComboBoxItem implements SelectableComboBoxItem {
    private final Sdk myJdk;
    public ActualJdkComboBoxItem(@NotNull Sdk jdk) { myJdk = jdk; }
    @Override public String toString() { return myJdk.getName(); }
    @NotNull @Override public Sdk getJdk() { return myJdk; }
    @Nullable @Override public String getSdkName() { return myJdk.getName(); }
    @Override public int hashCode() { return Objects.hash(myJdk); }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ActualJdkComboBoxItem item = (ActualJdkComboBoxItem)o;
      return myJdk.equals(item.myJdk);
    }
  }

  public static class ProjectJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem, SelectableComboBoxItem {
    @NotNull
    @Override
    public SdkListItem getItem() {
      return new SdkListItem.ProjectSdkItem();
    }

    @Override
    public int hashCode() {
      return 42;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof ProjectJdkComboBoxItem;
    }
  }

  public static class NoneJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem, SelectableComboBoxItem {
    @NotNull @Override public SdkListItem getItem() { return new SdkListItem.NoneSdkItem(); }
    public String toString() { return "<None>"; }
    @Override public int hashCode() { return 42; }
    @Override public boolean equals(Object obj) { return obj instanceof NoneJdkComboBoxItem; }
  }

  public static class ActionJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem, SelectableComboBoxItem {
    private final SdkListItem.ActionItem myItem;
    public ActionJdkComboBoxItem(SdkListItem.ActionItem item) { myItem = item; }
    @Override public @NotNull SdkListItem getItem() { return myItem; }
    @Override public @NotNull String toString() { return myItem.toString(); }
  }

  public static class SuggestedJdkComboBoxItem extends JdkComboBoxItem implements InnerComboBoxItem, SelectableComboBoxItem {
    private final SdkListItem.SuggestedItem myItem;
    public SuggestedJdkComboBoxItem(SdkListItem.SuggestedItem item) { myItem = item; }
    @Override public @NotNull SdkListItem getItem() { return myItem; }
    @Override public @NotNull String toString() { return "%s %s".formatted(myItem.sdkType.getName(), myItem.version); }
  }
}
