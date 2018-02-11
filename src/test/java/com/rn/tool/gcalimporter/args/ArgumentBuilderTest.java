package com.rn.tool.gcalimporter.args;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Paths;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.junit.runners.JUnit4.class)
public class ArgumentBuilderTest {

  @Test
  public void proceedWithNoArgs() throws Exception {
    Optional<Argument> ret = ArgumentBuilder.proceed();

    assertThat(ret, is(Optional.empty()));
  }

  @Test(expected = RuntimeException.class)
  public void proceedWithFile() throws Exception {
    ArgumentBuilder.proceed("-f");
  }

  @Test
  public void proceedWithFileAndRequiredParam() throws Exception {
    Argument expected = Argument.builder().settingFile(Paths.get("hoge.xml")).build();

    Optional<Argument> ret = ArgumentBuilder.proceed("-f", expected.getSettingFile().toString());

    assertThat(ret, is(Optional.of(expected)));
  }
}