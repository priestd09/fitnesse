// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.testsystems.slim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fitnesse.testsystems.ExecutionResult;
import fitnesse.testsystems.TestPage;
import fitnesse.testsystems.TestSummary;
import fitnesse.testsystems.slim.tables.ScenarioTable;

public class SlimTestContextImpl implements SlimTestContext {
  private final Map<String, String> symbols = new HashMap<>();
  private final Map<String, ScenarioTable> scenarios = new HashMap<>();
  private final TestSummary testSummary = new TestSummary();
  private final TestPage pageToTest;
  private List<ScenarioTable> sortedTables = null;

  public SlimTestContextImpl(TestPage pageToTest) {
    this.pageToTest = pageToTest;
  }

  @Override
  public String getSymbol(String symbolName) {
    return symbols.get(symbolName);
  }

  @Override
  public void setSymbol(String symbolName, String value) {
    symbols.put(symbolName, value);
  }

  @Override
  public void addScenario(String scenarioName, ScenarioTable scenarioTable) {
    if (sortedTables != null && !scenarios.containsValue(scenarioTable)) {
      sortedTables = null;
    }
    scenarios.put(scenarioName, scenarioTable);
  }

  @Override
  public ScenarioTable getScenario(String scenarioName) {
    return scenarios.get(scenarioName);
  }

  @Override
  public ScenarioTable getScenarioByPattern(String invokingString,
                                            CustomComparatorRegistry customComparatorRegistry) {
    ScenarioTable result = null;
    for (ScenarioTable s : getScenariosWithMostArgumentsFirst()) {
      s.setCustomComparatorRegistry(customComparatorRegistry);
      String[] args = s.matchParameters(invokingString);
      if (args != null) {
        result = s;
        break;
      }
    }
    return result;
  }

  private List<ScenarioTable> getScenariosWithMostArgumentsFirst() {
    if (sortedTables == null) {
      Collection<ScenarioTable> scenarioMap = getScenarios();
      sortedTables = new ArrayList<>(scenarioMap);
      Collections.sort(sortedTables, new ScenarioTableLengthComparator());
    }
    return sortedTables;
  }

  private static class ScenarioTableLengthComparator implements java.util.Comparator<ScenarioTable> {
    @Override
    public int compare(ScenarioTable st1, ScenarioTable st2) {
      int size1 = st1.getInputs().size();
      int size2 = st2.getInputs().size();
      return size2 - size1;
    }
  }

  @Override
  public Collection<ScenarioTable> getScenarios() {
    return scenarios.values();
  }

  @Override
  public void incrementPassedTestsCount() {
    increment(ExecutionResult.PASS);
  }

  @Override
  public void incrementFailedTestsCount() {
    increment(ExecutionResult.FAIL);
  }

  @Override
  public void incrementErroredTestsCount() {
    increment(ExecutionResult.ERROR);
  }

  @Override
  public void incrementIgnoredTestsCount() {
    increment(ExecutionResult.IGNORE);
  }

  @Override
  public void increment(ExecutionResult result) {
    this.testSummary.add(result);
  }

  @Override
  public void increment(TestSummary testSummary) {
    this.testSummary.add(testSummary);
  }

  public TestSummary getTestSummary() {
    return testSummary;
  }

  @Override
  public TestPage getPageToTest() {
    return pageToTest;
  }
}
