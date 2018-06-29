package com.longforus.mvpautocodeplus

import com.intellij.CommonBundle
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.WriteActionAware
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.PlatformIcons
import com.longforus.mvpautocodeplus.maker.TemplateMaker
import com.longforus.mvpautocodeplus.maker.TemplateParamFactory
import com.longforus.mvpautocodeplus.maker.createFileFromTemplate
import com.longforus.mvpautocodeplus.maker.getContractName
import com.longforus.mvpautocodeplus.ui.EnterKeywordDialog

/**
 * Created by XQ Yang on 2018/6/25  13:43.
 * Description :
 */

class MainAction : AnAction("main", "auto make mvp code", PlatformIcons.CLASS_ICON), WriteActionAware {
    var project: Project? = null
//    val createQueue = LinkedList<CreateTask>()

    fun createFile(enterName: String, templateName: String, dir: PsiDirectory, superImplName: String): PsiFile? {
        log.info("enterName = $enterName  template = $templateName  dir = $dir")
        val template = TemplateMaker.getTemplate(templateName, project!!)

//        ImplementAbstractClassMethodsFix()

//        object : WriteCommandAction(project, file) {
//            @Throws(Throwable::class)
//            protected override fun run(result: Result<*>) {
//                var newExpression = JavaPsiFacade.getElementFactory(project).createExpressionFromText(startElement.getText() + "{}", startElement) as PsiNewExpression
//                newExpression = startElement.replace(newExpression)
//                val psiClass = newExpression.anonymousClass ?: return
//                val subst = HashMap<PsiClass, PsiSubstitutor>()
//                for (selectedElement in selectedElements) {
//                    val baseClass = selectedElement.getElement().getContainingClass()
//                    if (baseClass != null) {
//                        var substitutor: PsiSubstitutor? = subst[baseClass]
//                        if (substitutor == null) {
//                            substitutor = TypeConversionUtil.getSuperClassSubstitutor(baseClass!!, psiClass, PsiSubstitutor.EMPTY)
//                            subst[baseClass] = substitutor
//                        }
//                        selectedElement.setSubstitutor(substitutor)
//                    }
//                }
//                OverrideImplementUtil.overrideOrImplementMethodsInRightPlace(editor, psiClass, selectedElements, false,
//                    true)
//            }
//        }.execute()


//        OverrideImplementUtil.overrideOrImplementMethods()
        val psiFile = createFileFromTemplate(enterName, template, dir, null, true, TemplateParamFactory.getParam4TemplateName(templateName, enterName, superImplName))
//        return make(enterName, templateName, dir, project)
//        return make4Template(enterName, templateName, dir, project!!)
        return psiFile
    }


    fun getActionName(directory: PsiDirectory?, newName: String?, templateName: String?): String {
        return "create mvp file"
    }

    private fun buildDialog(project: Project?, directory: PsiDirectory?, builder: CreateFileFromTemplateDialog.Builder?) {
        builder?.setTitle(directory?.name)
        builder?.addKind("Java Contract", com.intellij.icons.AllIcons.Nodes.Interface, CONTRACT_TP_NAME_JAVA)
        builder?.addKind("Java Contract+activity", PlatformIcons.JAVA_OUTSIDE_SOURCE_ICON, "2")
        builder?.addKind("Java Contract+fragment", PlatformIcons.JAVA_OUTSIDE_SOURCE_ICON, "3")
        builder?.addKind("Kotlin Contract", PlatformIcons.JAVA_OUTSIDE_SOURCE_ICON, "4")
        builder?.addKind("Kotlin Contract+activity", PlatformIcons.JAVA_OUTSIDE_SOURCE_ICON, "5")
        builder?.addKind("Kotlin Contract+fragment", PlatformIcons.JAVA_OUTSIDE_SOURCE_ICON, "6")
        builder?.setValidator(object : InputValidator {
            override fun checkInput(inputString: String?): Boolean {
                return !inputString.isNullOrEmpty()
            }

            override fun canClose(inputString: String?): Boolean {
                return true
            }

        })
    }


//    override fun actionPerformed(e: AnActionEvent?) {
//        val project = e?.getData(PlatformDataKeys.PROJECT)
//        val editor = e?.getData(PlatformDataKeys.EDITOR)
//        val data = e?.getData(PlatformDataKeys.SELECTED_ITEM)
//        val currentEditorFile = PsiUtilBase.getPsiFileInEditor(editor!!,project!!)
//        var currentEditorFileName = currentEditorFile?.getName()
//        EnterKeywordDialog.getDialog {
//            Messages.showMessageDialog(it,e?.dataContext?.toString() ?:"no",null)
//        }
//
//    }

    protected val log = Logger.getInstance("#com.intellij.ide.actions.CreateFromTemplateAction")



    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return
        project = CommonDataKeys.PROJECT.getData(dataContext)
        val dir = view.orChooseDirectory

        if (dir == null || project == null) return

        EnterKeywordDialog.getDialog {
            if (it.isJava) {
                createFile(it.name, CONTRACT_TP_NAME_JAVA, dir, "")
                if (!it.vImpl.isEmpty()) {
                    if (it.isActivity) {
                        createFile(it.name, VIEW_IMPL_TP_ACTIVITY_JAVA, dir, it.vImpl)
                    } else {
                        createFile(it.name, VIEW_IMPL_TP_FRAGMENT_JAVA, dir, it.vImpl)
                    }
                }
                if (!it.pImpl.isEmpty()) {
                    createFile(it.name, PRESENTER_IMPL_TP_JAVA, dir, it.pImpl)
                }
                if (!it.mImpl.isEmpty()) {
                    createFile(it.name, MODEL_IMPL_TP_JAVA, dir, it.mImpl)
                }

            } else {
                createFile(getContractName(it.name), CONTRACT_TP_NAME_KOTLIN, dir, "")
                if (!it.vImpl.isEmpty()) {
                    if (it.isActivity) {
                        createFile(it.name, VIEW_IMPL_TP_ACTIVITY_KOTLIN, dir, it.vImpl)
                    } else {
                        createFile(it.name, VIEW_IMPL_TP_FRAGMENT_KOTLIN, dir, it.vImpl)
                    }
                }
                if (!it.pImpl.isEmpty()) {
                    createFile(it.name, PRESENTER_IMPL_TP_KOTLIN, dir, it.pImpl)
                }
                if (!it.mImpl.isEmpty()) {
                    createFile(it.name, MODEL_IMPL_TP_KOTLIN, dir, it.mImpl)
                }
            }
        }

//        val builder = CreateFileFromTemplateDialog.createDialog(project!!)
//        buildDialog(project, dir, builder)
//        val selectedTemplateName = Ref.create<String>(null)
//        val createdElement = builder.show<PsiFile>(getErrorTitle(), getDefaultTemplateName(dir), object : CreateFileFromTemplateDialog.FileCreator<PsiFile> {
//
//            override fun createFile(name: String, templateName: String): PsiFile? {
//                selectedTemplateName.set(templateName)
//                return this@MainAction.createFile(name, templateName, dir)
//            }
//
//            override fun startInWriteAction(): Boolean {
//                return this@MainAction.startInWriteAction()
//            }
//
//            override fun getActionName(name: String, templateName: String): String {
//                return this@MainAction.getActionName(dir, name, templateName)
//            }
//        })
//        if (createdElement != null) {
//            view.selectElement(createdElement)
//            postProcess(createdElement, selectedTemplateName.get(), builder.customProperties)
//        }
    }

    protected fun postProcess(createdElement: PsiFile, templateName: String, customProperties: Map<String, String>?) {

    }


    protected fun getDefaultTemplateName(dir: PsiDirectory): String? {
        val property = getDefaultTemplateProperty()
        return if (property == null) null else PropertiesComponent.getInstance(dir.project).getValue(property)
    }

    protected fun getDefaultTemplateProperty(): String? {
        return null
    }

    override fun update(e: AnActionEvent?) {
        val dataContext = e!!.dataContext
        val presentation = e.presentation

        val enabled = isAvailable(dataContext)

        presentation.isVisible = enabled
        presentation.isEnabled = enabled
    }

    protected fun isAvailable(dataContext: DataContext): Boolean {
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        return project != null && view != null && view.directories.isNotEmpty()
    }


    protected fun getErrorTitle(): String {
        return CommonBundle.getErrorTitle()
    }

    //todo append $END variable to templates?
    fun moveCaretAfterNameIdentifier(createdElement: PsiNameIdentifierOwner) {
        val project = createdElement.project
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        if (editor != null) {
            val virtualFile = createdElement.containingFile.virtualFile
            if (virtualFile != null) {
                if (FileDocumentManager.getInstance().getDocument(virtualFile) === editor.document) {
                    val nameIdentifier = createdElement.nameIdentifier
                    if (nameIdentifier != null) {
                        editor.caretModel.moveToOffset(nameIdentifier.textRange.endOffset)
                    }
                }
            }
        }
    }

}