package ormx;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.List;

public class ClauseArgs {
  
  public final StringBuilder clause = new StringBuilder();
  public final List<Object> args = new ArrayList<Object>();
  public int groupCount;

  @Override public String toString() {
    return clause.toString();
  }
  
  public void set(ClauseArgs clause) {
    this.clause.append(clause.clause);
    this.args.addAll(clause.args);
    this.groupCount = clause.groupCount;
  }
  
  public void reset() {
    OrmUtils.clear(clause);
    args.clear();
    groupCount = 0;
  }
  
  public ClauseArgs group_start() {
    groupCount++;
    return this;
  }
  
  public ClauseArgs group_end() {
    clause.append(QueryBuilder.END_PARENT);
    return this;
  }
  
  public void append_group_start() {
    if (groupCount > 0) {
      clause.append(QueryBuilder.STAR_PARENT);
      groupCount--;
    }
  }
  
  public void clause(String type, String column, String op, Object value) {
    if (clause.length() > 0) clause.append(type);
    append_group_start();
    clause.append(column.trim()).append(QueryBuilder.SPACE);
    clause.append(op.trim()).append(QueryBuilder.SPACE);
    clause.append(QueryBuilder.INTERROGATION);
    args.add(value);
  }
  
  @SuppressLint("NewApi")
  public void cluseRaw(String type, String whereClause, Object... whereArgs) {
    if (whereClause != null && !whereClause.isEmpty()) {
      if (clause.length() > 0) clause.append(type);
      append_group_start();
      clause.append(whereClause);
      OrmUtils.fill(args, whereArgs);
    }
  }
  
  public void clause_in(String wh, String column, boolean not, Object... values) {
  	if (clause.length() > 0) clause.append(wh);
  	append_group_start();
  	clause.append(column).append(not ? " NOT IN" : " IN").append(QueryBuilder.STAR_PARENT);

  	for (int i = 0; i < values.length; i++) {
  		if (i > 0) clause.append(QueryBuilder.COMMA);
  		clause.append(QueryBuilder.INTERROGATION);
  		args.add(values[i]);
  	}

  	clause.append(QueryBuilder.END_PARENT);
  }
}
