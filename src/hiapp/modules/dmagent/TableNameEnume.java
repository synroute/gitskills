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
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", getAbbr() + "." + "IID");
			map.put("COLUMNNAMECH", "导入批次id");
			map.put("COLUMNDESCRIPTION", "导入批次id");
			map.put("dataType", "varchar");
			map.put("LENGTH", 50);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("COLUMNNAME", getAbbr() + "." + "CID");
			map2.put("COLUMNNAMECH", "客户id");
			map2.put("COLUMNDESCRIPTION", "客户唯一标识");
			map2.put("dataType", "varchar");
			map2.put("LENGTH", 50);

			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("COLUMNNAME", getAbbr() + "." + "MODIFYUSERID");
			map3.put("COLUMNNAMECH", "修改用户ID");
			map3.put("COLUMNDESCRIPTION", "修改用户ID");
			map3.put("dataType", "varchar");
			map3.put("LENGTH", 50);

			Map<String, Object> map4 = new HashMap<String, Object>();
			map4.put("COLUMNNAME", getAbbr() + "." + "MODIFYTIME");
			map4.put("COLUMNNAMECH", "修改日期时间");
			map4.put("COLUMNDESCRIPTION", "修改日期时间");
			map4.put("dataType", "datetime");
			map4.put("LENGTH", -1);
			
			list.add(map);
			list.add(map2);
			list.add(map3);
			list.add(map4);
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
			map.put("COLUMNNAME", getAbbr() + "." + "PRESETTIME");
			map.put("COLUMNNAMECH", "预约日期时间");
			map.put("COLUMNDESCRIPTION", "预约日期时间");
			map.put("dataType", "datetime");
			map.put("LENGTH", -1);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("COLUMNNAME", getAbbr() + "." + "STATE");
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
	RESULTTABLENAME("HAU_DM_B", "C_RESULT") {
		@Override
		public List<Map<String, Object>> getCandidadeColumn() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("COLUMNNAME", getAbbr() + "." + "DIALTYPE");
			map.put("COLUMNNAMECH", "拨打类型");
			map.put("COLUMNDESCRIPTION", "拨打类型");
			map.put("dataType", "varchar");
			map.put("LENGTH", 50);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("COLUMNNAME", getAbbr() + "." + "DIALTIME");
			map2.put("COLUMNNAMECH", "拨打时间");
			map2.put("COLUMNDESCRIPTION", "拨打时间");
			map2.put("dataType", "datetime");
			map2.put("LENGTH", -1);

			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("COLUMNNAME", getAbbr() + "." + "CUSTOMERCALLID");
			map3.put("COLUMNNAMECH", "呼叫流水号");
			map3.put("COLUMNDESCRIPTION", "呼叫流水号");
			map3.put("dataType", "varchar");
			map3.put("LENGTH", 50);
			
			Map<String, Object> map4 = new HashMap<String, Object>();
			map4.put("COLUMNNAME", getAbbr() + "." + "SOURCEID");
			map4.put("COLUMNNAMECH", "来源编号");
			map4.put("COLUMNDESCRIPTION", "来源编号");
			map4.put("dataType", "varchar");
			map4.put("LENGTH", 50);
			
			Map<String, Object> map5 = new HashMap<String, Object>();
			map3.put("COLUMNNAME", getAbbr() + "." + "ENDCODETYPE");
			map3.put("COLUMNNAMECH", "结束码类型");
			map3.put("COLUMNDESCRIPTION", "结束码类型");
			map3.put("dataType", "varchar");
			map3.put("LENGTH", 50);
			
			Map<String, Object> map6 = new HashMap<String, Object>();
			map4.put("COLUMNNAME", getAbbr() + "." + "ENDCODE");
			map4.put("COLUMNNAMECH", "结束码");
			map4.put("COLUMNDESCRIPTION", "结束码");
			map4.put("dataType", "varchar");
			map4.put("LENGTH", 50);

			list.add(map);
			list.add(map2);
			list.add(map3);
			list.add(map4);
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
