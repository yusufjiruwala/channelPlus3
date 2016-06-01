package com.doc.views.Query;

import java.util.List;

import com.generic.qryColumn;

public interface tableDataListner {
	public String calcSummary(List<String> qcGroup, qryColumn qc);

	public void beforeQuery();

	public void afterQuery();

	public String getCellStyle(qryColumn qc, int recordNo);

	public void afterVisual();
}
