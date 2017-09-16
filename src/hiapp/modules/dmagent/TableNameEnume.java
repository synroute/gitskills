package hiapp.modules.dmagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TableNameEnume {
	INPUTTABLENAME("HAU_DM_B", "C_IMPORT") {
		@Override
		public List<Map<String, Object>> getCandidadeColumn() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			return list;
		}

		@Override
		public String getAbbr() {
			return "DR";
		}
	},
	PRESETTABLENAME("HASYS_DM_B", "C_PRESETTIME") {
		@Override
		public List<Map<String, Object>> getCandidadeColumn() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", getAbbr() + "." + "PresetTime");
			map.put("COLUMNNAMECH", "预约时间");
			map.put("COLUMNDESCRIPTION", "预约时间");
			map.put("dataType", "Date");
			map.put("LENGTH", -1);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("COLUMNNAME", getAbbr() + "." + "State");
			map2.put("COLUMNNAMECH", "预约状态");
			map2.put("COLUMNDESCRIPTION", "预约状态");
			map2.put("dataType", "Varchar2(50)");
			map2.put("LENGTH", 50);

			list.add(map);
			list.add(map2);

			return list;
		}

		@Override
		public String getAbbr() {
			return "YY";
		}
	},
	JIEGUOTABLENAME("HAU_DM_B", "C_RESULT") {
		@Override
		public List<Map<String, Object>> getCandidadeColumn() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", getAbbr() + "." + "IID");
			map.put("COLUMNNAMECH", "导入批次id");
			map.put("COLUMNDESCRIPTION", "导入批次id");
			map.put("dataType", "Varchar2(50)");
			map.put("LENGTH", 50);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("COLUMNNAME", getAbbr() + "." + "CID");
			map2.put("COLUMNNAMECH", "客户id");
			map2.put("COLUMNDESCRIPTION", "客户id");
			map2.put("dataType", "Varchar2(50)");
			map2.put("LENGTH", 50);

			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("COLUMNNAME", getAbbr() + "." + "ModifyUserID");
			map3.put("COLUMNNAMECH", "修改人工号");
			map3.put("COLUMNDESCRIPTION", "修改人工号");
			map3.put("dataType", "Varchar2(50)");
			map3.put("LENGTH", 50);

			Map<String, Object> map4 = new HashMap<String, Object>();
			map4.put("COLUMNNAME", getAbbr() + "." + "ModifyTime");
			map4.put("COLUMNNAMECH", "修改时间");
			map4.put("COLUMNDESCRIPTION", "修改时间");
			map4.put("dataType", "Date");
			map4.put("LENGTH", -1);

			Map<String, Object> map5 = new HashMap<String, Object>();
			map5.put("COLUMNNAME", getAbbr() + "." + "OptrType");
			map5.put("COLUMNNAMECH", "拨打类型");
			map5.put("COLUMNDESCRIPTION", "拨打提交；修改提交");
			map5.put("dataType", "Varchar2(10)");
			map5.put("LENGTH", 10);

			Map<String, Object> map6 = new HashMap<String, Object>();
			map6.put("COLUMNNAME", getAbbr() + "." + "DialTime");
			map6.put("COLUMNNAMECH", "拨打时间");
			map6.put("COLUMNDESCRIPTION", "拨打时间");
			map6.put("dataType", "Date");
			map6.put("LENGTH", -1);

			Map<String, Object> map7 = new HashMap<String, Object>();
			map7.put("COLUMNNAME", getAbbr() + "." + "CustomerCallId");
			map7.put("COLUMNNAMECH", "呼叫流水号");
			map7.put("COLUMNDESCRIPTION", "呼叫流水号");
			map7.put("dataType", "int");
			map7.put("LENGTH", 0);

			list.add(map);
			list.add(map2);
			list.add(map3);
			list.add(map4);
			list.add(map5);
			list.add(map6);
			list.add(map7);
			return list;
		}

		@Override
		public String getAbbr() {
			return "JG";
		}
	};
	private String prefix;
	private String suffix;

	TableNameEnume(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public abstract List<Map<String, Object>> getCandidadeColumn();

	public abstract String getAbbr();
}
