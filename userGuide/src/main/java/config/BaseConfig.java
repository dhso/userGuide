package config;

import modules.crm.controller.CrmController;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;

import frame.interceptor.ReqResInViewInterceptor;
import frame.plugin.shiro.core.ShiroInterceptor;
import frame.plugin.shiro.core.ShiroPlugin;
import frame.plugin.shiro.freemarker.ShiroTags;
import frame.plugin.tablebind.AutoTableBindPlugin;
import frame.plugin.tablebind.SimpleNameStyles;

public class BaseConfig extends JFinalConfig {

	private Routes routes;

	public void configConstant(Constants me) {
		// 加载配置/国际化
		PropKit.use("config.txt");
		me.setI18nDefaultBaseName("i18n");
		me.setI18nDefaultLocale("zh_CN");
		me.setDevMode(PropKit.getBoolean("devMode", false));
	}

	public void configRoute(Routes me) {
		this.routes = me;
		me.add("/crm", CrmController.class, "/crm");// CRM
	}

	public void configPlugin(Plugins me) {
		// 添加shiro支持
		me.add(new ShiroPlugin(routes));
		// 添加缓存支持
		me.add(new EhCachePlugin(BaseConfig.class.getClassLoader().getResource("ehcache-model.xml")));
		// 配置数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("jdbcUser"), PropKit.get("jdbcPassword"), PropKit.get("jdbcDriver"));
		WallFilter wallFilter = new WallFilter();
		wallFilter.setDbType(JdbcConstants.MYSQL);
		druidPlugin.addFilter(wallFilter);
		druidPlugin.addFilter(new StatFilter());
		me.add(druidPlugin);
		// 添加自动绑定model与表插件
		AutoTableBindPlugin autoTableBindPlugin = new AutoTableBindPlugin(druidPlugin, SimpleNameStyles.LOWER_UNDERLINE);
		autoTableBindPlugin.setShowSql(true);
		autoTableBindPlugin.setContainerFactory(new CaseInsensitiveContainerFactory());
		autoTableBindPlugin.setDevMode(PropKit.getBoolean("devMode", false));
		me.add(autoTableBindPlugin);
	}

	public void configInterceptor(Interceptors me) {
		// shiro拦截器
		me.add(new ShiroInterceptor());
		// shiroFreemarker拦截器
		// me.add(new ShiroFreemarkerTemplateInterceptor());
		// 让 模版 可以使用session
		me.add(new SessionInViewInterceptor());
		// 让 模版 可以使用request/response
		me.add(new ReqResInViewInterceptor());
		// 让 模版 可以使用I18n
		me.add(new I18nInterceptor());
	}

	public void configHandler(Handlers me) {
		me.add(new UrlSkipHandler(".*/static/.*", false));
		me.add(new ContextPathHandler("baseUrl"));
		me.add(new DruidStatViewHandler("/druid"));
	}

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/", 5);
	}

	public void afterJFinalStart() {
		super.afterJFinalStart();
		FreeMarkerRender.getConfiguration().setSharedVariable("shiro", new ShiroTags());
	}
}