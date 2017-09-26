package com.jetcms.cms.template;


public class CmsModuleGenerator {
	private static String packName = "com.jetcms.cms.template";
	private static String fileName = "jetcms.properties";

	public static void main(String[] args) {
		new ModuleGenerator(packName, fileName).generate();
	}
}
