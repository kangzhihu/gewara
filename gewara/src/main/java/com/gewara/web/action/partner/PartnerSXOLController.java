package com.gewara.web.action.partner;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gewara.constant.PaymethodConstant;
import com.gewara.constant.content.SignName;
import com.gewara.constant.ticket.PartnerConstant;
import com.gewara.model.api.ApiUser;
import com.gewara.model.content.GewaCommend;
import com.gewara.model.movie.Movie;
import com.gewara.support.ErrorCode;
import com.gewara.untrans.CommonService;
import com.gewara.util.BeanUtil;
import com.gewara.util.WebUtils;
import com.gewara.web.util.PageUtil;
/**
 * 
 *�������� ������Ʊ
 */
@Controller
public class PartnerSXOLController extends BasePartnerController {
	@Autowired@Qualifier("commonService")
	private CommonService commonService;
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	private ApiUser getApiUser(){
		return daoService.getObject(ApiUser.class, PartnerConstant.PARTNER_SXOL);
	}
	private static final int pageSize = 10;
	@RequestMapping("/partner/sxol/index.xhtml")
	public String index(String moviename, ModelMap model, 
			@CookieValue(required=false,value="ukey") String ukey, HttpServletResponse response,HttpServletRequest request,Integer pageNo){
		ApiUser partner = getApiUser();
		if(StringUtils.isBlank(ukey)) PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/sxol/");
		List<Movie> movieList = getOpenMovieListByDate(partner, partner.getDefaultCity(), null);
		int rowsCount = StringUtils.isBlank(moviename) ? movieList.size() : 1;
		if(pageNo==null) pageNo = 0;
		PageUtil pageUtil = new PageUtil(rowsCount , pageSize, pageNo,"partner/sxol/index.xhtml", true, true);
		pageUtil.initPageInfo(request.getParameterMap());
		movieList = BeanUtil.getSubList(movieList, pageNo*pageSize, pageSize);
		model.put("pageUtil", pageUtil);
		model.put("movieList", movieList);
		super.filterMovieList(moviename, movieList, model);
		model.put("partner", partner);
		model.put("paramsStr", "");
		return "partner/sxol/index.vm";
	}
	
	@RequestMapping("/partner/sxol/movieDetail.xhtml")
	public String movieDetail(Long movieid, @CookieValue(required=false,value="ukey") String ukey,Date fyrq, 
			HttpServletResponse response, ModelMap model){
		ApiUser partner = getApiUser();
		Movie movie = daoService.getObject(Movie.class, movieid);
		if(movie==null) return forwardMessage(model, "ӰƬ�����ڣ�");
		opiList(partner, movieid, fyrq, "partner/sxol/step1.vm", model);
		model.put("movie", movie);
		if(StringUtils.isBlank(ukey)) PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/sxol/");
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		model.put("paramsStr", "");
		return "partner/sxol/movieDetail.vm";
	}
	
	//�ڶ�����ѡ����λ
	@RequestMapping("/partner/sxol/chooseSeat.xhtml")
	public String chooseSeat(Long mpid,@CookieValue(required=false,value="ukey") String ukey,  HttpServletResponse response, ModelMap model){
		if(mpid==null) return showError(model, "ȱ�ٲ�����");
		ApiUser apiUser = getApiUser();
		if(StringUtils.isBlank(ukey)) ukey = PartnerUtil.setUkCookie(response, config.getBasePath() + "partner/sxol/");
		return chooseSeat(apiUser, mpid, ukey, "partner/sxol/chooseSeat.vm", model);
	}
	
	@RequestMapping("/partner/sxol/seatPage.xhtml")
	public String seatPage(Long mpid, Long partnerid, Integer price,  @CookieValue(value="ukey", required=false) String ukey, ModelMap model){
		model.put("mpid", mpid);
		model.put("partnerid", partnerid);
		model.put("price", price);
		model.put("ukey", ukey);
		ErrorCode<String> code = addSeatData(mpid, partnerid, ukey, model);
		if(!code.isSuccess()) return showJsonError(model, code.getMsg());
		return "partner/new_seatPage.vm";
	}
	//������:����λ���Ӷ���
	@RequestMapping("/partner/sxol/addOrder.xhtml")
	public String addOrder(String captchaId, String captcha, Long mpid,
			@CookieValue(value="ukey", required=false) String ukey,
			@RequestParam("mobile")String mobile,
			@RequestParam("seatid")String seatid, HttpServletRequest request, ModelMap model){
		ApiUser partner = getApiUser();
		boolean validCaptcha = controllerService.validateCaptcha(captchaId, captcha, WebUtils.getRemoteIp(request));
		if(!validCaptcha) return showJsonError(model, "��֤�����");
		String paymethod = PaymethodConstant.PAYMETHOD_ALIPAY;
		ErrorCode code = addOrder(mpid, mobile, seatid, ukey, partner, partner.getBriefname(), null, null, paymethod, WebUtils.getRemoteIp(request), model);
		if(code.isSuccess()) {
			return showJsonSuccess(model, model.get("orderId")+"");
		}
		return showJsonError(model, code.getMsg());
	}
	//���Ĳ���ȷ�϶���ȥ֧�������²鿴��
	@RequestMapping("/partner/sxol/showOrder.xhtml")
	public String showOrder(Long orderId, ModelMap model, 
			  @CookieValue(value="ukey", required=false) String ukey,
			String phonenumber,String checkvalue){
		ApiUser partner = getApiUser();
		model.put("phonenumber", phonenumber);
		model.put("checkvalue", checkvalue);
		List<GewaCommend> gcList = commonService.getGewaCommendListByRelatedid(null, SignName.PARTNER_AD, partner.getId(), null, true, 0, 1);
		if(gcList.size()>0){
			model.put("gewaCommend", gcList.get(0));
		}
		return showOrder(ukey, orderId, partner, "partner/sxol/confirmOrder.vm", model);
	}
	//���Ĳ���ȷ�϶���ȥ֧��
	@RequestMapping(method=RequestMethod.POST,value="/partner/sxol/saveOrder.xhtml")
	public String saveOrder(ModelMap model, String mobile,@CookieValue(value="ukey", required=false) String ukey,
			@RequestParam("orderId")long orderId){
		String paymethod = PaymethodConstant.PAYMETHOD_ALIPAY;
		return saveOrder(orderId, mobile, paymethod, "", ukey, model);
	}
}