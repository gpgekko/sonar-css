/*
 * Sonar CSS Plugin
 * Copyright (C) 2013 Tamas Kende
 * kende.tamas@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.css;

import org.sonar.api.batch.rule.Checks;

import org.junit.Ignore;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.css.ast.visitors.SonarComponents;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CssSquidSensorTest {

  private CssSquidSensor sensor;
  private ModuleFileSystem fileSystem;
  private FileLinesContextFactory fileLinesContextFactory;
  private CheckFactory checkFactory;

  @Before
  public void setUp() {
    fileLinesContextFactory = mock(FileLinesContextFactory.class);
    FileLinesContext fileLinesContext = mock(FileLinesContext.class);
    when(fileLinesContextFactory.createFor(Mockito.any(Resource.class))).thenReturn(fileLinesContext);

    fileSystem = mock(ModuleFileSystem.class);
    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(Arrays.asList(new File("src/test/resources/org/sonar/plugins/css/cssProject/css/boxSizing.css")));
    when(fileSystem.sourceCharset()).thenReturn(Charset.forName("UTF-8"));

    checkFactory = mock(CheckFactory.class);
    when(checkFactory.create(Mockito.anyString())).thenReturn(mock(Checks.class)); //not sure what to mock here

    sensor = new CssSquidSensor(mock(RulesProfile.class), null, fileSystem, mock(CheckFactory.class));
  }

  @Test
  public void should_execute_on() {
    Project project = new Project("key");
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    CssSquidSensor cssSensor = new CssSquidSensor(mock(RulesProfile.class), mock(SonarComponents.class), fs, mock(CheckFactory.class));

    when(fs.files(Mockito.any(FileQuery.class))).thenReturn(ListUtils.EMPTY_LIST);
    assertThat(cssSensor.shouldExecuteOnProject(project)).isFalse();

    when(fs.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.of(new File("/tmp")));
    assertThat(cssSensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Ignore
  @Test
  public void should_analyse() {
    Project project = new Project("key");
    addProjectFileSystem(project);
    SensorContext context = mock(SensorContext.class);

    sensor.analyse(project, context);

    verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.FILES), Mockito.eq(1.0));
    verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.LINES), Mockito.eq(34.0));
    verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.NCLOC), Mockito.eq(24.0));
    verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.STATEMENTS), Mockito.eq(18.0));
    verify(context).saveMeasure(Mockito.any(Resource.class), Mockito.eq(CoreMetrics.COMMENT_LINES), Mockito.eq(5.0));
  }

  /**
   * This is unavoidable in order to be compatible with sonarqube 4.2
   */
  private void addProjectFileSystem(Project project) {
    ProjectFileSystem fs = mock(ProjectFileSystem.class);
    when(fs.getSourceDirs()).thenReturn(Arrays.asList(new File("src/test/resources/org/sonar/plugins/css/cssProject/css/")));

    project.setFileSystem(fs);
  }


}
