package org.dasxunya.diploma;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
//нужен для генерации структуры файлов в тестах
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.PsiTreeUtil;
import org.dasxunya.diploma.Generator.UnitTestsGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;


/**
 * TODO:
 * создание параметризованных тестовых методов:
 *  2) Если класс - генерировать по каждому методу класса:
 *      - проверить, был ли сгенерирован метод ранее (по инстансу - если так можно)
 *          если был: скип
 *          иначе: генерация тестового метода
 *  3) Если метод - генерировать тестовый метод по выбранному методу:
 *      - проверить был ли сгенерирован метод ранее
 *          если был: скип
 *          иначе: генерация тестового метода
 * <p>
 * По какому принципу генерировать? (посмотреть значения для типов данных в )
 * 1) примитивы
 * 2) Объектные классы примитивов
 * 3) Enums???
 * 4)
 **/
public class UnitTestsPlugin extends AnAction {

    //region Поля
    private UnitTestsGenerator generator;
    //endregion

    //region Конструкторы
    public UnitTestsPlugin() {
        this.generator = new UnitTestsGenerator();
    }
    //endregion

    //region Методы

    private void showMessage(Project project, String message) {
        Messages.showMessageDialog(project, message,
                "Info", Messages.getInformationIcon());
    }

    //region Отладка
    private void println(String message) {
        System.out.println(message);
    }

    private void print(String message) {
        System.out.print(message);
    }

    private void print(PsiMethod psiMethod) {

    }

    private void print(PsiClass psiClass) {

    }
    //endregion

    //endregion

    private static PsiMethod findMethod(PsiElement element) {
        PsiMethod method = (element instanceof PsiMethod) ? (PsiMethod) element :
                PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null && method.getContainingClass() != null) {
            return findMethod(method.getParent());
        }
        return method;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try {
            String unitTestStr = this.generator.Generate(anActionEvent.getData(CommonDataKeys.PSI_ELEMENT));
            println(unitTestStr);
        } catch (Exception ex) {
            showMessage(anActionEvent.getProject(), ex.getMessage());
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        super.update(anActionEvent);
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}