package org.dasxunya.diploma.generator;

import org.dasxunya.diploma.constants.Constants;
import org.dasxunya.diploma.constants.TestType;
import org.junit.jupiter.api.Test;

public class PsiClassGeneratorTests extends BaseTest {

    @Test
    void testGenerateClassTests() {
        String fileName = "CarTests";
        String psiClassTestStr = this.generator.generate(this.mockPsiClass, TestType.PARAMETERIZED);
        this.saveFile(psiClassTestStr, this.actualFolderPath,
                "CarTests", Constants.Strings.Extensions.txt);
        try {
            this.compareFilesByName(fileName, fileName, Constants.Strings.Extensions.txt);
        } catch (Exception e) {
            this.print(e.getMessage());
        }
        this.deleteFile(this.actualFolderPath, fileName, Constants.Strings.Extensions.txt);
    }


}
