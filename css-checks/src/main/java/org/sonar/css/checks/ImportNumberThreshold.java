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
package org.sonar.css.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.css.parser.CssGrammar;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

/**
 * @author tkende
 *
 */
@Rule(key = "S2735", priority = Priority.CRITICAL, cardinality = Cardinality.SINGLE)
@BelongsToProfile(title = CheckList.REPOSITORY_NAME, priority = Priority.MAJOR)
public class ImportNumberThreshold extends SquidCheck<LexerlessGrammar> {

  private static final int DEFAULT_THRESHOLD = 31;

  private int currentImportCount;

  @Override
  public void init() {
    subscribeTo(CssGrammar.AT_RULE);
  }

  @Override
  public void visitFile(AstNode astNode) {
    currentImportCount = 0;
  }

  @Override
  public void visitNode(AstNode astNode) {
    if ("import".equals(astNode.getFirstChild(CssGrammar.AT_KEYWORD).getFirstChild(CssGrammar.IDENT).getTokenValue())) {
      currentImportCount++;
    }
  }

  @Override
  public void leaveFile(AstNode astNode) {
    if (currentImportCount > DEFAULT_THRESHOLD) {
      getContext().createLineViolation(this, "This sheet imports {0,number,#} other sheets, {1,number,#}" +
          " more than the {2,number,#} maximum.",
        astNode, currentImportCount, currentImportCount - DEFAULT_THRESHOLD, DEFAULT_THRESHOLD);
    }
  }
}
