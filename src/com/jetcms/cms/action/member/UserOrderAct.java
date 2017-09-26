package com.jetcms.cms.action.member;

import static com.jetcms.cms.Constants.TPLDIR_MEMBER;
import static com.jetcms.common.page.SimplePage.cpn;































import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jetcms.cms.entity.main.ContentBuy;
import com.jetcms.cms.manager.assist.CmsAccountDrawMng;
import com.jetcms.cms.manager.main.ContentBuyMng;
import com.jetcms.cms.manager.main.ContentChargeMng;
import com.jetcms.common.page.Pagination;
import com.jetcms.common.web.CookieUtils;
import com.jetcms.core.entity.CmsRole;
import com.jetcms.core.entity.CmsSite;
import com.jetcms.core.entity.CmsUser;
import com.jetcms.core.entity.CmsUserExt;
import com.jetcms.core.entity.MemberConfig;
import com.jetcms.core.manager.CmsUserMng;
import com.jetcms.core.web.WebErrors;
import com.jetcms.core.web.util.CmsUtils;
import com.jetcms.core.web.util.FrontUtils;

/**
 * 用户账户相关
 * 包含笔者所写文章被用户购买记录
 * 自己的消费记录
 */
@Controller
public class UserOrderAct {
	private double countzd=0;//总单
	private double countze=0;//总额
	private double grzd=0;  //个人销售总单
	private double grze=0;//个人销售总额
	private double ygzd=0;//员工销售总单
	private double ygze=0;//员工销售总额
	private double dlzd=0;//代理销售总单
	private double dlze=0;//代理销售总额
	private double mycountzd=0;//总单
	private double mycountze=0;//总额
	private double mygrzd=0;  //当前用户销售总单
	private double mygrze=0;//当前用户销售总额
	private double myygzd=0;//当前用户销售总单
	private double myygze=0;//当前用户销售总额
	private double mydlzd=0;//当前用销售总单
	private double mydlze=0;//当前用销售总额
	/**
	 * 登录用户统计其本月的销量
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list1.do")
	public String orderList1(String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		Pagination pagination=contentBuyMng.getPageOrder(startTime,endTime,user.getId(),
				cpn(pageNo), CookieUtils.getPageSize(request));
		for (Object obj : pagination.getList()) {
			ContentBuy b = (ContentBuy)obj;
			System.out.println(b.getBuyUser().getRealname());
			System.out.println(b.getContent().getTitle());
		}
		System.out.println(user.getRealname());
		model.addAttribute("pagination", pagination);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		return "admin/order/list";
	}
	/**
	 * 统计
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list2.do")
	public String orderList2(String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser currUser = CmsUtils.getUser(request);
		//当前级别
		String roleName="线下省运";
		String url="../global_sy/v_list.do";
		//指向统计的url
		String urlTj="../member/order_list4.do";
		//线下代理总数
		int countDL =0;
		//线下员工总数
		int countYG =0;
		//线下用户总数
		int countXX=0;
		Set<CmsRole> roles = currUser.getRoles();
		int roleId=0;
		for (CmsRole cmsRole : roles) {
			roleId = cmsRole.getId();
		}
		Pagination paginationDL = null;
		Pagination paginationYG = manager.getPageHY(null, null,
				null, null, null, true, currUser.getRank(),
				null,null,6,
				null,null,
				cpn(pageNo), CookieUtils.getPageSize(request),currUser.getId());
		if(paginationYG.getList()!=null){
			countYG = paginationYG.getList().size();
		}
		Pagination paginationXX = manager.getPagexx(null, null,
				null, null, null, false, null, null,
				null,null,null,null,cpn(pageNo),
				CookieUtils.getPageSize(request),currUser.getId());
		if(paginationXX.getList()!=null){
			countXX = paginationXX.getList().size();
		}
		//3省运 4会员5代理   其他角色给予权限进来就是管理员
		if(roleId==3){
			roleName = "线下会员";
			url="../global_xxhy/v_list.do?userId="+currUser.getId();
			urlTj="../member/order_list9.do";
			paginationDL = manager.getPageHY(null, null,
					null, null, false, true, currUser.getRank(),
					null,null,4,
					null,null,
					cpn(pageNo), CookieUtils.getPageSize(request),currUser.getId());
		}else if(roleId==4){
			roleName="线下代理";
			url="../global_xxdl/v_list.do?userId="+currUser.getId();
			urlTj="../member/order_list10.do";
			paginationDL = manager.getPageHY(null, null,
					null, null, false, true, currUser.getRank(),
					null,null,5,
					null,null,
					cpn(pageNo), CookieUtils.getPageSize(request),currUser.getId());
		}else if(roleId==5){
			roleName="";
			url="#";
			urlTj="../member/order_list10.do";
			countDL= 0;
		}else{
			//管理员
		paginationDL = manager.getPage(null, null,
				null, null, false, true, currUser.getRank(),
				null,null,3,
				null,null,
				cpn(pageNo), CookieUtils.getPageSize(request));
		
		}
		if(paginationDL!=null){
			if(paginationDL.getList()!=null){
				countDL = paginationDL.getList().size();
			}
		}
		model.addAttribute("countDL", countDL);
		model.addAttribute("roleName", roleName);
		model.addAttribute("countYG", countYG);
		model.addAttribute("countXX", countXX);
		model.addAttribute("url", url);
		model.addAttribute("urlTj", urlTj);
		model.addAttribute("userId",currUser.getId());
		return "admin/order/Statistics";
	}
	/**
	 * 会员总业绩统计
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list3.do")
	public String orderList3(String orderNum,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		Set<CmsRole> roles = user.getRoles();
		 
		 return "";
	}
	/**
	 * 省运总业绩统计 进入该方法的只有管理员
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list4.do")
	public String orderList4(String url,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		init();
		CmsUser user = CmsUtils.getUser(request);
		//查询所有省运用户 统计当前分页销售总额
		Pagination paginationSY = manager.getPage(null, null,
				null, null, false, true, 9,
				null,null,3,
				null,null,
				1, 1000);
		List<CmsUser> cms = new ArrayList<CmsUser>();
		for (Object obj : paginationSY.getList()) {
			init();
			CmsUser cm = (CmsUser) obj;
			//遍历所有是否存在个人业务
			Pagination pgr = contentBuyMng.getPageOrder(startTime,endTime,cm.getId(),
					1, 1000);
			if(pgr.getList()==null){
				grzd=0;
				grze=0;
			}else{
				grzd = pgr.getList().size();
				for (Object o1 : pgr.getList()) {
					ContentBuy b = (ContentBuy)o1;
					grze+=b.getChargeAmount();
				}
			}
			//遍历所有员工业务
			Pagination paginationYG = manager.getPageHY(null, null,
					null, null, null, true, user.getRank(),
					null,null,6,
					null,null,
					1, 1000,cm.getId());
			if(paginationYG.getList()==null){
				ygzd=0;
				ygze=0;
			}else{
				for (Object o1 : paginationYG.getList()) {
					CmsUser cmsyyg = (CmsUser) o1;
					Pagination pgrsyyg = contentBuyMng.getPageOrder(startTime,endTime,cmsyyg.getId(),
							cpn(pageNo), 1000);
					if(pgrsyyg.getList()==null){
						ygzd=0;
						ygze=0;
					}else{
						ygzd += pgrsyyg.getList().size();
						for (Object o2 : pgrsyyg.getList()) {
							ContentBuy b = (ContentBuy)o2;
							ygze+=b.getChargeAmount();
						}
					}
				}
			}
			//省运下所有会员
			Pagination paginationHY = manager.getPageHY(null, null,
					null, null, false, true, 9,
					null,null,4,
					null,null,
					1, 1000,cm.getId());
			if(paginationHY.getList()==null){
				dlzd+=0;
				dlze+=0;
			}else{
				for (Object ob : paginationHY.getList()) {
					CmsUser cmhy= (CmsUser) ob;
					counthy(cmhy.getId(), startTime, endTime);
				}
				
			}
			cm.setGrzd(grzd);
			cm.setGrze(grze);
			cm.setYgzd(ygzd);
			cm.setYgze(ygze);
			cm.setDlzd(dlzd);
			cm.setDlze(dlze);
			countzd = grzd+ygzd+dlzd;
			countze = grze+ygze+dlze;
			cm.setCountzd(countzd);
			cm.setCountze(countze);
			mycountzd +=countzd;
			mycountze +=countze;
			cms.add(cm);
		}
		paginationSY.setList(cms);
		model.addAttribute("mycountzd", mycountzd);
		model.addAttribute("mycountze", mycountze);
		model.addAttribute("mygrzd", mygrzd);
		model.addAttribute("mygrze", mygrze);
		model.addAttribute("myygzd", myygzd);
		model.addAttribute("myygze", myygze);
		model.addAttribute("mydlzd", mydlzd);
		model.addAttribute("mydlze", mydlze);
		model.addAttribute("pagination", paginationSY);
		model.addAttribute("userName", user.getRealname());
		model.addAttribute("url",url);
		model.addAttribute("startTime",startTime);
		model.addAttribute("endTime",endTime);
		return "admin/order/order";
	}
	/**
	 * 统计会员销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public void counthy(int userId,String startTime,String endTime) {
		//统计传入会员的销售总额
		//会员个人账单
		Pagination pgrhy = contentBuyMng.getPageOrder(startTime,endTime,userId,
				1, 1000);
		if(pgrhy.getList()==null){
			dlzd+=0;
			dlze+=0;
		}else{
			dlzd += pgrhy.getList().size();
			for (Object o1 : pgrhy.getList()) {
				ContentBuy b = (ContentBuy)o1;
				dlze+=b.getChargeAmount();
			}
		}
		//遍历会员员工所有业务
		Pagination phyyg = manager.getPageHY(null, null,
				null, null, null, true, 8,
				null,null,6,
				null,null,
				1, 1000,userId);
		if(phyyg.getList()==null){
			dlzd+=0;
			dlze+=0;
		}else{
			for (Object o1 : phyyg.getList()) {
				CmsUser cmhy= (CmsUser) o1;
				Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
						1, 1000);
				if(hyygorder.getList()==null){
					dlzd+=0;
					dlze+=0;
				}else{
					dlzd += hyygorder.getList().size();
					for (Object o2 : hyygorder.getList()) {
						ContentBuy b = (ContentBuy)o2;
						dlze+=b.getChargeAmount();
					}
				}
			}
		}
		//会员下所有代理
		Pagination paginationDL = manager.getPageHY(null, null,
				null, null, false, true, 9,
				null,null,5,
				null,null,
				1, 1000,userId);
		if(paginationDL.getList()==null){
			dlzd+=0;
			dlze+=0;
		}else{
			for (Object ob : paginationDL.getList()) {
				CmsUser cmhy= (CmsUser) ob;
				countdl(cmhy.getId(), startTime, endTime);
			}
		}
	}
	/**
	 * 统计某个代理销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public void countdl(int userId,String startTime,String endTime) {
		//遍历当前代理个人账单
		Pagination pgrdl = contentBuyMng.getPageOrder(startTime,endTime,userId,
				1, 1000);
		if(pgrdl.getList()==null){
			dlzd+=0;
			dlze+=0;
		}else{
			dlzd+=pgrdl.getList().size();
			for (Object o2 : pgrdl.getList()) {
				ContentBuy b = (ContentBuy)o2;
				dlze+=b.getChargeAmount();
			}
		}
		//遍历代理员工下所有账单
		Pagination phyyg = manager.getPageHY(null, null,
				null, null, null, true, 7,
				null,null,6,
				null,null,
				1, 1000,userId);
		if(phyyg.getList()==null){
			dlzd+=0;
			dlze+=0;
		}else{
			for (Object o1 : phyyg.getList()) {
				CmsUser cmhy= (CmsUser) o1;
				Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
						1, 1000);
				if(hyygorder.getList()==null){
					dlzd+=0;
					dlze+=0;
				}else{
					dlzd += hyygorder.getList().size();
					for (Object o2 : hyygorder.getList()) {
						ContentBuy b = (ContentBuy)o2;
						dlze+=b.getChargeAmount();
					}
				}
			}
		}
	}
	//查询某个用户的线下用户
	public List<CmsUser> listxx(int userId){
		manager.getPagexx(null, null,
				null, null, null, false, null, null,
				null,null,null,null,1,
				1000,userId);
		return null;
	}
	public   void init(){
		 grzd=0;  //个人销售总单
		 grze=0;//个人销售总额
		 ygzd=0;//员工销售总单
		 ygze=0;//员工销售总额
		 dlzd=0;//代理销售总单
		 dlze=0;//代理销售总额
		 countzd= 0;
		 countze =0;
		 mycountzd= 0;
		 mycountze =0;
	}
	/**
	 * 查看个人业绩详情
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list5.do")
	public String orderList5(Integer userId,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		boolean look = false;
		Set<CmsRole> roles = user.getRoles();
		int roleId=0;
		for (CmsRole cmsRole : roles) {
			roleId = cmsRole.getId();
		}
		if(roleId==3||roleId==4||roleId==5){
			look=true;
		}
		Pagination pagination=contentBuyMng.getPageOrder(startTime,endTime,userId,
				cpn(pageNo), CookieUtils.getPageSize(request));
		if(look){
			List<ContentBuy> list = new ArrayList<ContentBuy>();
			for (Object ob : pagination.getList()) {
				ContentBuy buy = (ContentBuy) ob;
				CmsUser u = new CmsUser();
				u = buy.getBuyUser();
				//登录名马赛克 截取前5位
				String loginName = buy.getBuyUser().getUsername();
				u.setUsername(loginName.substring(0,5)+"*****");
				String relName = buy.getBuyUser().getRealname();
				if(relName!=null){
					int i = relName.length();
					StringBuffer first = new StringBuffer();
					first.append(relName.substring(0,1));
					for (int j = 0; j < i-1; j++) {
						first.append("*");
					}
					u.setRealnameJd(first.toString());
					buy.setBuyUser(u);
				}
				list.add(buy);	
			};
			pagination.setList(list);
		}
		model.addAttribute("pagination", pagination);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		return "admin/order/list";
	}
	/**
	 * 统计当前省运下会员销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list7.do")
	public String orderList7(Integer userId,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		//遍历当前省运下所有会员
		init();
		Pagination	paginationhy = manager.getPageHY(null, null,
				null, null, false, true, 9,
				null,null,4,
				null,null,
				1,1000,userId);
		List<CmsUser> list = new ArrayList<CmsUser>();
		if(paginationhy.getList()==null){
			
		}else{
			//会员个人账单
			for (Object obj : paginationhy.getList()) {
				init();
				CmsUser cm = (CmsUser) obj;
				Pagination pgrhy = contentBuyMng.getPageOrder(startTime,endTime,cm.getId(),
						1, 1000);
				if(pgrhy.getList()==null){
					grzd=0;
					grze=0;
				}else{
					grzd += pgrhy.getList().size();
					for (Object o11 : pgrhy.getList()) {
						ContentBuy b = (ContentBuy)o11;
						grze+=b.getChargeAmount();
					}
				}
				//遍历会员员工所有业务
				Pagination phyyg = manager.getPageHY(null, null,
						null, null, null, true, 9,
						null,null,6,
						null,null,
						1, 1000,cm.getId());
				if(phyyg.getList()==null){
					ygzd+=0;
					ygze+=0;
				}else{
					for (Object o11 : phyyg.getList()) {
						CmsUser cmhy= (CmsUser) o11;
						Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
								1, 1000);
						if(hyygorder.getList()==null){
							ygzd+=0;
							ygze+=0;
						}else{
							ygzd += hyygorder.getList().size();
							for (Object o2 : hyygorder.getList()) {
								ContentBuy b = (ContentBuy)o2;
								ygze+=b.getChargeAmount();
							}
						}
					}
				}
				//会员下所有代理
				Pagination paginationDL = manager.getPageHY(null, null,
						null, null, false, true, 9,
						null,null,5,
						null,null,
						1, 1000,cm.getId());
				if(paginationDL.getList()==null){
					dlzd+=0;
					dlze+=0;
				}else{
					for (Object ob : paginationDL.getList()) {
						CmsUser cmhy= (CmsUser) ob;
						countdl(cmhy.getId(), startTime, endTime);
					}
				}
				cm.setGrzd(grzd);
				cm.setGrze(grze);
				cm.setYgzd(ygzd);
				cm.setYgze(ygze);
				cm.setDlzd(dlzd);
				cm.setDlze(dlze);
				countzd = grzd+ygzd+dlzd;
				countze = grze+ygze+dlze;
				cm.setCountzd(countzd);
				cm.setCountze(countze);
				list.add(cm);
			}
		}
		paginationhy.setList(list);
		model.addAttribute("pagination", paginationhy);
		model.addAttribute("startTime",startTime);
		model.addAttribute("endTime",endTime);
		return "admin/order/orderHY";
	}
	/**
	 * 员工总业绩统计
	 * @param pageNo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list6.do")
	public String orderList6(Integer userId,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		init();
		if(userId==null){
			CmsUser user = CmsUtils.getUser(request);
			userId = user.getId();
		}
		//查看线下员工
		Pagination phyyg = manager.getPageHY(null, null,
				null, null, null, true, 9,
				null,null,6,
				null,null,
				1, 1000,userId);
		List<CmsUser> list = new  ArrayList<CmsUser>();
		if(phyyg.getList()==null){
			ygzd=0;
			ygzd=0;
		}else{
			for (Object o1 : phyyg.getList()) {
				init();
				CmsUser cmhy= (CmsUser) o1;
				Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
						1, 20);
				if(hyygorder.getList()==null){
					ygzd=0;
					ygze=0;
				}else{
					ygzd = hyygorder.getList().size();
					for (Object o2 : hyygorder.getList()) {
						ContentBuy b = (ContentBuy)o2;
						ygze+=b.getChargeAmount();
					}
				}
				cmhy.setYgzd(ygzd);
				cmhy.setYgze(ygze);
				list.add(cmhy);
			}
			phyyg.setList(list);
		} 
		model.addAttribute("pagination", phyyg);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		return "admin/order/listYg";
	}
	//导出员工总业绩 
	@RequestMapping(value = "/member/order_ExportCount.do")
	public void order_ExportCount(Integer userId,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("销售总额统计");
		HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
        HSSFCellStyle style = this.getStyle(workbook); 
        String[] rowName =  {"姓名","登录账号","个人销单","个人销额","开始时间","结束时间"} ;
        int columnNum = rowName.length;
        HSSFRow rowRowName = sheet.createRow(0);                // 在索引2的位置创建行(最顶端的行开始的第二行)
        
        // 将列头设置到sheet的单元格中
        for(int n=0;n<columnNum;n++){
            HSSFCell  cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
            cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
            HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
            cellRowName.setCellValue(text);                                    //设置列头单元格的值
            cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
        }
		init();
		if(userId==null){
			CmsUser user = CmsUtils.getUser(request);
			userId = user.getId();
		}
		//查看线下员工
		Pagination phyyg = manager.getPageHY(null, null,
				null, null, null, true, 9,
				null,null,6,
				null,null,
				1, 20,userId);
	
		List<CmsUser> list = new  ArrayList<CmsUser>();
		if(phyyg.getList()==null){
			ygzd=0;
			ygzd=0;
		}else{
			int i =0;
			for (Object o1 : phyyg.getList()) {
				HSSFRow row = sheet.createRow(i+1);
				i++;
				init();
				CmsUser cmhy= (CmsUser) o1;
				HSSFSheet details;
				if(true){//预留是否导出明细判断
					//导出员工明细数据
					details = ExportAll(workbook, cmhy);
				}
				Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
						1, 1000);
				if(hyygorder.getList()==null){
					ygzd=0;
					ygze=0;
				}else{
					ygzd = hyygorder.getList().size();
					int j =0;
					for (Object o2 : hyygorder.getList()) {
						
						ContentBuy b = (ContentBuy)o2;
						ygze+=b.getChargeAmount();
						//插入明细数据
						if(true){
							HSSFRow rowDeta = details.createRow(j+1);
							insertDetails(rowDeta,b,style);
							j++;
						}
					}
				}
				cmhy.setYgzd(ygzd);
				cmhy.setYgze(ygze);
				list.add(cmhy);
				//组装数据
				insertData(row,cmhy,startTime,endTime,style);
			}
			phyyg.setList(list);
		}try
        { 
			String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
	        String headStr = "attachment; filename=\"" + fileName + "\"";
	        response.setContentType("APPLICATION/OCTET-STREAM");
	        response.setHeader("Content-Disposition", headStr);
	        OutputStream out = response.getOutputStream();
	        workbook.write(out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
		//download("E://.xls",response);
	}
	
	//Excel下载
	 private void download(String path, HttpServletResponse response) {  
        try {  
            // path是指欲下载的文件的路径。  
            File file = new File(path);  
            // 取得文件名。  
            String filename = file.getName();  
            // 以流的形式下载文件。  
            InputStream fis = new BufferedInputStream(new FileInputStream(path));  
            byte[] buffer = new byte[fis.available()];  
            fis.read(buffer);  
            fis.close();  
            // 清空response  
            response.reset();  
            // 设置response的Header  
            response.addHeader("Content-Disposition", "attachment;filename="  
                    + new String(filename.getBytes()));  
            response.addHeader("Content-Length", "" + file.length());  
            OutputStream toClient = new BufferedOutputStream(  
                    response.getOutputStream());  
            response.setContentType("application/vnd.ms-excel;charset=gb2312");  
            toClient.write(buffer);  
            toClient.flush();  
            toClient.close();  
        } catch (IOException ex) {  
            ex.printStackTrace();  
        }  
    }
	
	
	/**
	 * 统计当前会员下代理销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list8.do")
	public String orderList8(Integer userId,String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		//遍历当前会员下所有代理业绩
		init();
		Pagination	paginationhy = manager.getPageHY(null, null,
				null, null, false, true, 9,
				null,null,5,
				null,null,
				1,1000,userId);
		List<CmsUser> list = new ArrayList<CmsUser>();
		if(paginationhy.getList()==null){
			
		}else{
			//会员线下所有代理
			for (Object obj : paginationhy.getList()) {
				init();
				CmsUser cm = (CmsUser) obj;
				//遍历当前代理个人账单
				Pagination pgrdl = contentBuyMng.getPageOrder(startTime,endTime,cm.getId(),
						1, 1000);
				if(pgrdl.getList()==null){
					grzd=0;
					grze=0;
				}else{
					grzd+=pgrdl.getList().size();
					for (Object o2 : pgrdl.getList()) {
						ContentBuy b = (ContentBuy)o2;
						grze+=b.getChargeAmount();
					}
				}
				//遍历代理员工下所有账单
				Pagination phyyg = manager.getPageHY(null, null,
						null, null, null, true, 9,
						null,null,6,
						null,null,
						1, 1000,userId);
				if(phyyg.getList()==null){
					ygzd=0;
					ygze=0;
				}else{
					for (Object o1 : phyyg.getList()) {
						CmsUser cmhy= (CmsUser) o1;
						Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
								1, 1000);
						if(hyygorder.getList()==null){
							ygzd+=0;
							ygze+=0;
						}else{
							ygzd += hyygorder.getList().size();
							for (Object o2 : hyygorder.getList()) {
								ContentBuy b = (ContentBuy)o2;
								ygze+=b.getChargeAmount();
							}
						}
					}
				}
				cm.setGrzd(grzd);
				cm.setGrze(grze);
				cm.setYgzd(ygzd);
				cm.setYgze(ygze);
				cm.setDlzd(dlzd);
				cm.setDlze(dlze);
				countzd = grzd+ygzd+dlzd;
				countze = grze+ygze+dlze;
				cm.setCountzd(countzd);
				cm.setCountze(countze);
				list.add(cm);
			}
		}
		paginationhy.setList(list);
		model.addAttribute("pagination", paginationhy);
		model.addAttribute("startTime",startTime);
		model.addAttribute("endTime",endTime);
		return "admin/order/orderDL";
	}
	
	
	/**
	 * 统计登录省运下会员销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list9.do")
	public String counthy(String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getId();
		//统计加载个人数据
		List<CmsUser> cms = new ArrayList<CmsUser>();
		init();
		Pagination paginationHY = manager.getPageHY(null, null,
				null, null, false, true, 9,
				null,null,4,
				null,null,
				1, 1000,userId);
		if(paginationHY.getList()==null){
		}else{
			for (Object ob : paginationHY.getList()) {
				init();
				CmsUser cm= (CmsUser) ob;
				Pagination pgrhy = contentBuyMng.getPageOrder(startTime,endTime,cm.getId(),
						1, 1000);
				if(pgrhy.getList()==null){
					grzd+=0;
					grze+=0;
				}else{
					grzd += pgrhy.getList().size();
					for (Object o1 : pgrhy.getList()) {
						ContentBuy b = (ContentBuy)o1;
						grze+=b.getChargeAmount();
					}
				}
				//遍历会员员工所有业务
				Pagination phyyg = manager.getPageHY(null, null,
						null, null, null, true, 8,
						null,null,6,
						null,null,
						1, 1000,cm.getId());
				if(phyyg.getList()==null){
					ygzd+=0;
					ygze+=0;
				}else{
					for (Object o1 : phyyg.getList()) {
						CmsUser cmhy1= (CmsUser) o1;
						Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy1.getId(),
								1, 1000);
						if(hyygorder.getList()==null){
							ygzd+=0;
							ygze+=0;
						}else{
							ygzd += hyygorder.getList().size();
							for (Object o2 : hyygorder.getList()) {
								ContentBuy b = (ContentBuy)o2;
								ygze+=b.getChargeAmount();
							}
						}
					}
				}
				//会员下所有代理
				Pagination paginationDL = manager.getPageHY(null, null,
						null, null, false, true, 9,
						null,null,5,
						null,null,
						1, 1000,cm.getId());
				if(paginationDL.getList()==null){
					dlzd+=0;
					dlze+=0;
				}else{
					for (Object ob1 : paginationDL.getList()) {
						CmsUser cmhy2= (CmsUser) ob1;
						countdl(cmhy2.getId(), startTime, endTime);
					}
					
				}
				cm.setGrzd(grzd);
				cm.setGrze(grze);
				cm.setYgzd(ygzd);
				cm.setYgze(ygze);
				cm.setDlzd(dlzd);
				cm.setDlze(dlze);
				countzd = grzd+ygzd+dlzd;
				countze = grze+ygze+dlze;
				mycountzd+=countzd;
				mycountze+=countze;
				cm.setCountzd(countzd);
				cm.setCountze(countze);
				cms.add(cm);
			}
		}
		paginationHY.setList(cms);
		model.addAttribute("pagination", paginationHY);
		model.addAttribute("mycountzd", mycountzd);
		model.addAttribute("mycountze", mycountze);
		model.addAttribute("userName", user.getRealname());
		model.addAttribute("startTime",startTime);
		model.addAttribute("endTime",endTime);
		return "admin/order/orderCountHY";
	}
	/**
	 * 统计登录会员下会员销售总额
	 * @param userId 代理id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/order_list10.do")
	public String countdl(String startTime,String endTime,Integer pageNo,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getId();
		//统计加载个人数据
		List<CmsUser> cms = new ArrayList<CmsUser>();
		init();
		Pagination paginationDL = manager.getPageHY(null, null,
				null, null, false, true, 9,
				null,null,5,
				null,null,
				1, 1000,userId);
		if(paginationDL.getList()==null){
		}else{
			for (Object ob : paginationDL.getList()) {
				init();
				CmsUser cm= (CmsUser) ob;
				Pagination pgrdl = contentBuyMng.getPageOrder(startTime,endTime,cm.getId(),
						1, 1000);
				if(pgrdl.getList()==null){
					grzd+=0;
					grze+=0;
				}else{
					grzd+=pgrdl.getList().size();
					for (Object o2 : pgrdl.getList()) {
						ContentBuy b = (ContentBuy)o2;
						grze+=b.getChargeAmount();
					}
				}
				//遍历代理员工下所有账单
				Pagination phyyg = manager.getPageHY(null, null,
						null, null, null, true, 7,
						null,null,6,
						null,null,
						1, 1000,userId);
				if(phyyg.getList()==null){
					ygzd+=0;
					ygze+=0;
				}else{
					for (Object o1 : phyyg.getList()) {
						CmsUser cmhy= (CmsUser) o1;
						Pagination hyygorder = contentBuyMng.getPageOrder(startTime,endTime,cmhy.getId(),
								1, 1000);
						if(hyygorder.getList()==null){
							ygzd+=0;
							ygze+=0;
						}else{
							ygzd += hyygorder.getList().size();
							for (Object o2 : hyygorder.getList()) {
								ContentBuy b = (ContentBuy)o2;
								ygze+=b.getChargeAmount();
							}
						}
					}
				cm.setGrzd(grzd);
				cm.setGrze(grze);
				cm.setYgzd(ygzd);
				cm.setYgze(ygze);
				countzd = grzd+ygzd+dlzd;
				countze = grze+ygze+dlze;
				mycountzd+=countzd;
				mycountze+=countze;
				cm.setCountzd(countzd);
				cm.setCountze(countze);
				cms.add(cm);
			}
		}
		}
		paginationDL.setList(cms);
		model.addAttribute("pagination", paginationDL);
		model.addAttribute("mycountzd", mycountzd);
		model.addAttribute("mycountze", mycountze);
		model.addAttribute("userName", user.getRealname());
		model.addAttribute("startTime",startTime);
		model.addAttribute("endTime",endTime);
		return "admin/order/orderCountDL";
	}
	@Autowired
	private ContentBuyMng contentBuyMng;
	@Autowired
	protected CmsUserMng manager;
		//excel组装
	 /* 
     * 列头单元格样式
     */    
      public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
          
            // 设置字体
          HSSFFont font = workbook.createFont();
          //设置字体大小
          font.setFontHeightInPoints((short)11);
          //字体加粗
          font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
          //设置字体名字 
          font.setFontName("Courier New");
          //设置样式; 
          HSSFCellStyle style = workbook.createCellStyle();
          //设置底边框; 
          style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
          //设置底边框颜色;  
          style.setBottomBorderColor(HSSFColor.BLACK.index);
          //设置左边框;   
          style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
          //设置左边框颜色; 
          style.setLeftBorderColor(HSSFColor.BLACK.index);
          //设置右边框; 
          style.setBorderRight(HSSFCellStyle.BORDER_THIN);
          //设置右边框颜色; 
          style.setRightBorderColor(HSSFColor.BLACK.index);
          //设置顶边框; 
          style.setBorderTop(HSSFCellStyle.BORDER_THIN);
          //设置顶边框颜色;  
          style.setTopBorderColor(HSSFColor.BLACK.index);
          //在样式用应用设置的字体;  
          style.setFont(font);
          //设置自动换行; 
          style.setWrapText(false);
          //设置水平对齐的样式为居中对齐;  
          style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
          //设置垂直对齐的样式为居中对齐; 
          style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
          
          return style;
          
      }
      
      /*  
     * 列数据信息单元格样式
     */  
      public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
            // 设置字体
            HSSFFont font = workbook.createFont();
            //设置字体大小
            //font.setFontHeightInPoints((short)10);
            //字体加粗
            //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            //设置字体名字 
            font.setFontName("Courier New");
            //设置样式; 
            HSSFCellStyle style = workbook.createCellStyle();
            //设置底边框; 
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            //设置底边框颜色;  
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            //设置左边框;   
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            //设置左边框颜色; 
            style.setLeftBorderColor(HSSFColor.BLACK.index);
            //设置右边框; 
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            //设置右边框颜色; 
            style.setRightBorderColor(HSSFColor.BLACK.index);
            //设置顶边框; 
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置顶边框颜色;  
            style.setTopBorderColor(HSSFColor.BLACK.index);
            //在样式用应用设置的字体;  
            style.setFont(font);
            //设置自动换行; 
            style.setWrapText(false);
            //设置水平对齐的样式为居中对齐;  
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐; 
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
           
            return style;
      
      }
      public void insertData(HSSFRow row ,CmsUser cmhy,String startTime,String endTime,HSSFCellStyle style){
    	  	HSSFCell  cell = row.createCell(0,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(cmhy.getRealname());
			cell.setCellStyle(style);
			cell = row.createCell(1,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(cmhy.getUsername());
			cell.setCellStyle(style);
			cell = row.createCell(2,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(cmhy.getYgzd());
			cell.setCellStyle(style);
			cell = row.createCell(3,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(cmhy.getYgze());
			cell.setCellStyle(style);
			cell = row.createCell(4,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(startTime);
			cell.setCellStyle(style);
			cell = row.createCell(5,HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(endTime);
			cell.setCellStyle(style);
      }
      //员工明细数据
      public HSSFSheet ExportAll(HSSFWorkbook workbook,CmsUser cmhy){
    	  String sheetName = cmhy.getRealname()==null?cmhy.getUsername():cmhy.getRealname();
    	  HSSFSheet sheet = workbook.createSheet(sheetName);
    	  HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
          HSSFCellStyle style = this.getStyle(workbook); 
          String[] rowName =  {"订单ID","订单号","购买人","联系方式","购买课程","购买时间"} ;
          int columnNum = rowName.length;
          HSSFRow rowRowName = sheet.createRow(0);                // 在索引2的位置创建行(最顶端的行开始的第二行)
          
          // 将列头设置到sheet的单元格中
          for(int n=0;n<columnNum;n++){
              HSSFCell  cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
              cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
              HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
              cellRowName.setCellValue(text);                                    //设置列头单元格的值
              cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
          }
          return sheet;
      }
      public void insertDetails(HSSFRow row ,ContentBuy b,HSSFCellStyle style){
    	  	HSSFCell  cell = row.createCell(0,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getId());
  			cell.setCellStyle(style);
  			cell = row.createCell(1,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getOrderNumber());
  			cell.setCellStyle(style);
  			cell = row.createCell(2,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getBuyUser().getRealname());
  			cell.setCellStyle(style);
  			cell = row.createCell(3,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getBuyUser().getUsername());
  			cell.setCellStyle(style);
  			cell = row.createCell(4,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getContent().getTitle());
  			cell.setCellStyle(style);
  			cell = row.createCell(5,HSSFCell.CELL_TYPE_STRING);
  			cell.setCellValue(b.getBuyTime()+"");
  			cell.setCellStyle(style);
      }
}
