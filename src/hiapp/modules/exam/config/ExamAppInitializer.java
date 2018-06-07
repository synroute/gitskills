package hiapp.modules.exam.config;


import hiapp.utils.spring.HiAppWebAnnotationConfigDispatcherServletInitializer;

public class ExamAppInitializer extends HiAppWebAnnotationConfigDispatcherServletInitializer{

	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return new Class<?>[] {ExamWebSocketConfig.class};
	}

}
