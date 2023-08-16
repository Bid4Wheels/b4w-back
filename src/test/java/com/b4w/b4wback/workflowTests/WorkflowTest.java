package com.b4w.b4wback.workflowTests;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class WorkflowTest {

    @Test
    public void test01_shouldSucceed() {
        Assert.assertEquals("This test should pass", "This test should pass");
    }

}