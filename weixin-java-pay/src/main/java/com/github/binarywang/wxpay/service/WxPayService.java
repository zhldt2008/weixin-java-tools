package com.github.binarywang.wxpay.service;

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.github.binarywang.wxpay.bean.WxPayApiData;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponInfoQueryRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponInfoQueryResult;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponSendRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponSendResult;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponStockQueryRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponStockQueryResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxScanPayNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayAuthcode2OpenidRequest;
import com.github.binarywang.wxpay.bean.request.WxPayMicropayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderReverseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayReportRequest;
import com.github.binarywang.wxpay.bean.request.WxPaySendRedpackRequest;
import com.github.binarywang.wxpay.bean.request.WxPayShorturlRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayBillResult;
import com.github.binarywang.wxpay.bean.result.WxPayMicropayResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderReverseResult;
import com.github.binarywang.wxpay.bean.result.WxPayRedpackQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPaySendRedpackResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;

/**
 * <pre>
 * 微信支付相关接口.
 * Created by Binary Wang on 2016/7/28.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public interface WxPayService {

  /**
   * 获取微信支付请求url前缀，沙箱环境可能不一样.
   */
  String getPayBaseUrl();

  /**
   * 发送post请求，得到响应字节数组.
   *
   * @param url        请求地址
   * @param requestStr 请求信息
   * @param useKey     是否使用证书
   * @return 返回请求结果字节数组
   */
  byte[] postForBytes(String url, String requestStr, boolean useKey) throws WxPayException;

  /**
   * 发送post请求，得到响应字符串.
   *
   * @param url        请求地址
   * @param requestStr 请求信息
   * @param useKey     是否使用证书
   * @return 返回请求结果字符串
   */
  String post(String url, String requestStr, boolean useKey) throws WxPayException;

  /**
   * 获取企业付款服务类.
   */
  EntPayService getEntPayService();

  /**
   * 设置企业付款服务类，允许开发者自定义实现类.
   */
  void setEntPayService(EntPayService entPayService);

  /**
   * <pre>
   * 查询订单.
   * 详见https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
   * 该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。
   * 需要调用查询接口的情况：
   * ◆ 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
   * ◆ 调用支付接口后，返回系统错误或未知交易状态情况；
   * ◆ 调用被扫支付API，返回USERPAYING的状态；
   * ◆ 调用关单或撤销接口API之前，需确认支付状态；
   * 接口地址：https://api.mch.weixin.qq.com/pay/orderquery
   * </pre>
   *
   * @param transactionId 微信订单号
   * @param outTradeNo    商户系统内部的订单号，当没提供transactionId时需要传这个。
   */
  WxPayOrderQueryResult queryOrder(String transactionId, String outTradeNo) throws WxPayException;

  /**
   * <pre>
   * 关闭订单.
   * 应用场景
   * 以下情况需要调用关单接口：
   * 1. 商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
   * 2. 系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
   * 注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。
   * 接口地址：https://api.mch.weixin.qq.com/pay/closeorder
   * 是否需要证书：   不需要。
   * </pre>
   *
   * @param outTradeNo 商户系统内部的订单号
   */
  WxPayOrderCloseResult closeOrder(String outTradeNo) throws WxPayException;

  /**
   * 调用统一下单接口，并组装生成支付所需参数对象.
   *
   * @param request 统一下单请求参数
   * @param <T>     请使用{@link com.github.binarywang.wxpay.bean.order}包下的类
   * @return 返回 {@link com.github.binarywang.wxpay.bean.order}包下的类对象
   */
  <T> T createOrder(WxPayUnifiedOrderRequest request) throws WxPayException;

  /**
   * 统一下单(详见https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1)
   * 在发起微信支付前，需要调用统一下单接口，获取"预支付交易会话标识"
   * 接口地址：https://api.mch.weixin.qq.com/pay/unifiedorder
   *
   * @param request 请求对象，注意一些参数如appid、mchid等不用设置，方法内会自动从配置对象中获取到（前提是对应配置中已经设置）
   */
  WxPayUnifiedOrderResult unifiedOrder(WxPayUnifiedOrderRequest request) throws WxPayException;

  /**
   * 该接口调用“统一下单”接口，并拼装发起支付请求需要的参数.
   * 详见https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5
   *
   * @param request 请求对象，注意一些参数如appid、mchid等不用设置，方法内会自动从配置对象中获取到（前提是对应配置中已经设置）
   * @deprecated 建议使用 {@link com.github.binarywang.wxpay.service.WxPayService#createOrder(WxPayUnifiedOrderRequest)}
   */
  @Deprecated
  Map<String, String> getPayInfo(WxPayUnifiedOrderRequest request) throws WxPayException;

  /**
   * 获取配置.
   */
  WxPayConfig getConfig();

  /**
   * 设置配置对象.
   */
  void setConfig(WxPayConfig config);

  /**
   * <pre>
   * 微信支付-申请退款.
   * 详见 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
   * 接口链接：https://api.mch.weixin.qq.com/secapi/pay/refund
   * </pre>
   *
   * @param request 请求对象
   * @return 退款操作结果
   */
  WxPayRefundResult refund(WxPayRefundRequest request) throws WxPayException;

  /**
   * <pre>
   * 微信支付-查询退款.
   * 应用场景：
   *  提交退款申请后，通过调用该接口查询退款状态。退款有一定延时，用零钱支付的退款20分钟内到账，
   *  银行卡支付的退款3个工作日后重新查询退款状态。
   * 详见 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_5
   * 接口链接：https://api.mch.weixin.qq.com/pay/refundquery
   * </pre>
   * 以下四个参数四选一
   *
   * @param transactionId 微信订单号
   * @param outTradeNo    商户订单号
   * @param outRefundNo   商户退款单号
   * @param refundId      微信退款单号
   * @return 退款信息
   */
  WxPayRefundQueryResult refundQuery(String transactionId, String outTradeNo, String outRefundNo, String refundId)
    throws WxPayException;

  /**
   * @see WxPayService#parseOrderNotifyResult(String).
   * @deprecated use {@link WxPayService#parseOrderNotifyResult(String)} instead
   */
  @Deprecated
  WxPayOrderNotifyResult getOrderNotifyResult(String xmlData) throws WxPayException;

  /**
   * 解析支付结果通知.
   * 详见https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
   */
  WxPayOrderNotifyResult parseOrderNotifyResult(String xmlData) throws WxPayException;

  /**
   * 解析退款结果通知
   * 详见https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_16&index=9
   */
  WxPayRefundNotifyResult parseRefundNotifyResult(String xmlData) throws WxPayException;

  /**
   * 解析扫码支付回调通知
   * 详见https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_4
   */
  WxScanPayNotifyResult parseScanPayNotifyResult(String xmlData) throws WxPayException;

  /**
   * 发送微信红包给个人用户.
   * <pre>
   * 文档详见:
   * 发送普通红包 https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3
   *  接口地址：https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack
   * 发送裂变红包 https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_5&index=4
   *  接口地址：https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack
   * </pre>
   *
   * @param request 请求对象
   */
  WxPaySendRedpackResult sendRedpack(WxPaySendRedpackRequest request) throws WxPayException;

  /**
   * <pre>
   *   查询红包记录.
   *   用于商户对已发放的红包进行查询红包的具体信息，可支持普通红包和裂变包。
   *   请求Url：https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo
   *   是否需要证书：是（证书及使用说明详见商户证书）
   *   请求方式：POST
   * </pre>
   *
   * @param mchBillNo 商户发放红包的商户订单号，比如10000098201411111234567890
   */
  WxPayRedpackQueryResult queryRedpack(String mchBillNo) throws WxPayException;

  /**
   * <pre>
   * 扫码支付模式一生成二维码的方法。
   * 二维码中的内容为链接，形式为：
   * weixin://wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX
   * 其中XXXXX为商户需要填写的内容，商户将该链接生成二维码，如需要打印发布二维码，需要采用此格式。商户可调用第三方库生成二维码图片。
   * 文档详见: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_4
   * </pre>
   *
   * @param productId  产品Id
   * @param sideLength 要生成的二维码的边长，如果为空，则取默认值400
   * @param logoFile   商户logo图片的文件对象，可以为空
   * @return 生成的二维码的字节数组
   */
  byte[] createScanPayQrcodeMode1(String productId, File logoFile, Integer sideLength);

  /**
   * <pre>
   * 扫码支付模式一生成二维码的方法.
   * 二维码中的内容为链接，形式为：
   * weixin://wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX
   * 其中XXXXX为商户需要填写的内容，商户将该链接生成二维码，如需要打印发布二维码，需要采用此格式。商户可调用第三方库生成二维码图片。
   * 文档详见: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_4
   * </pre>
   *
   * @param productId 产品Id
   * @return 生成的二维码URL连接
   */
  String createScanPayQrcodeMode1(String productId);

  /**
   * <pre>
   * 扫码支付模式二生成二维码的方法.
   * 对应链接格式：weixin：//wxpay/bizpayurl?sr=XXXXX。请商户调用第三方库将code_url生成二维码图片。
   * 该模式链接较短，生成的二维码打印到结账小票上的识别率较高。
   * 文档详见: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_5
   * </pre>
   *
   * @param codeUrl    微信返回的交易会话的二维码链接
   * @param logoFile   商户logo图片的文件对象，可以为空
   * @param sideLength 要生成的二维码的边长，如果为空，则取默认值400
   * @return 生成的二维码的字节数组
   */
  byte[] createScanPayQrcodeMode2(String codeUrl, File logoFile, Integer sideLength);

  /**
   * <pre>
   * 交易保障.
   * 应用场景：
   *  商户在调用微信支付提供的相关接口时，会得到微信支付返回的相关信息以及获得整个接口的响应时间。
   *  为提高整体的服务水平，协助商户一起提高服务质量，微信支付提供了相关接口调用耗时和返回信息的主动上报接口，
   *  微信支付可以根据商户侧上报的数据进一步优化网络部署，完善服务监控，和商户更好的协作为用户提供更好的业务体验。
   * 接口地址： https://api.mch.weixin.qq.com/payitil/report
   * 是否需要证书：不需要
   * </pre>
   */
  void report(WxPayReportRequest request) throws WxPayException;

  /**
   * <pre>
   * 下载对账单.
   * 商户可以通过该接口下载历史交易清单。比如掉单、系统错误等导致商户侧和微信侧数据不一致，通过对账单核对后可校正支付状态。
   * 注意：
   * 1、微信侧未成功下单的交易不会出现在对账单中。支付成功后撤销的交易会出现在对账单中，跟原支付单订单号一致，bill_type为REVOKED；
   * 2、微信在次日9点启动生成前一天的对账单，建议商户10点后再获取；
   * 3、对账单中涉及金额的字段单位为“元”。
   * 4、对账单接口只能下载三个月以内的账单。
   * 接口链接：https://api.mch.weixin.qq.com/pay/downloadbill
   * 详情请见: <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_6">下载对账单</a>
   * </pre>
   *
   * @param billDate   对账单日期 bill_date 下载对账单的日期，格式：20140603
   * @param billType   账单类型 bill_type ALL，返回当日所有订单信息，默认值，SUCCESS，返回当日成功支付的订单，REFUND，返回当日退款订单
   * @param tarType    压缩账单 tar_type 非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。
   * @param deviceInfo 设备号 device_info 非必传参数，终端设备号
   * @return 保存到本地的临时文件
   */
  WxPayBillResult downloadBill(String billDate, String billType, String tarType, String deviceInfo) throws WxPayException;

  /**
   * <pre>
   * 提交刷卡支付.
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_10&index=1
   * 应用场景：
   * 收银员使用扫码设备读取微信用户刷卡授权码以后，二维码或条码信息传送至商户收银台，由商户收银台或者商户后台调用该接口发起支付。
   * 提醒1：提交支付请求后微信会同步返回支付结果。当返回结果为“系统错误”时，商户系统等待5秒后调用【查询订单API】，查询支付实际交易结果；当返回结果为“USERPAYING”时，商户系统可设置间隔时间(建议10秒)重新查询支付结果，直到支付成功或超时(建议30秒)；
   * 提醒2：在调用查询接口返回后，如果交易状况不明晰，请调用【撤销订单API】，此时如果交易失败则关闭订单，该单不能再支付成功；如果交易成功，则将扣款退回到用户账户。当撤销无返回或错误时，请再次调用。注意：请勿扣款后立即调用【撤销订单API】,建议至少15秒后再调用。撤销订单API需要双向证书。
   * 接口地址：   https://api.mch.weixin.qq.com/pay/micropay
   * 是否需要证书：不需要。
   * </pre>
   */
  WxPayMicropayResult micropay(WxPayMicropayRequest request) throws WxPayException;

  /**
   * <pre>
   * 撤销订单API.
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_11&index=3
   * 应用场景：
   *  支付交易返回失败或支付系统超时，调用该接口撤销交易。如果此订单用户支付失败，微信支付系统会将此订单关闭；
   *  如果用户支付成功，微信支付系统会将此订单资金退还给用户。
   *  注意：7天以内的交易单可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API。
   *  提交支付交易后调用【查询订单API】，没有明确的支付结果再调用【撤销订单API】。
   *  调用支付接口后请勿立即调用撤销订单API，建议支付后至少15s后再调用撤销订单接口。
   *  接口链接 ：https://api.mch.weixin.qq.com/secapi/pay/reverse
   *  是否需要证书：请求需要双向证书。
   * </pre>
   */
  WxPayOrderReverseResult reverseOrder(WxPayOrderReverseRequest request) throws WxPayException;

  /**
   * <pre>
   *  转换短链接.
   *  文档地址：
   *     https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_9&index=8
   *  应用场景：
   *     该接口主要用于扫码原生支付模式一中的二维码链接转成短链接(weixin://wxpay/s/XXXXXX)，减小二维码数据量，提升扫描速度和精确度。
   *  接口地址：https://api.mch.weixin.qq.com/tools/shorturl
   *  是否需要证书：否
   * </pre>
   *
   * @param request 请求对象
   */
  String shorturl(WxPayShorturlRequest request) throws WxPayException;

  /**
   * <pre>
   *  转换短链接.
   * </pre>
   *
   * @param longUrl 需要被压缩的网址
   * @see WxPayService#shorturl(WxPayShorturlRequest)
   */
  String shorturl(String longUrl) throws WxPayException;

  /**
   * <pre>
   * 授权码查询OPENID接口.
   *    通过授权码查询公众号Openid，调用查询后，该授权码只能由此商户号发起扣款，直至授权码更新。
   * 文档地址：
   *    https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_13&index=9
   * 接口链接:
   *    https://api.mch.weixin.qq.com/tools/authcodetoopenid
   * </pre>
   *
   * @param request 请求对象
   * @return openid
   */
  String authcode2Openid(WxPayAuthcode2OpenidRequest request) throws WxPayException;

  /**
   * <pre>
   * 授权码查询OPENID接口.
   * </pre>
   *
   * @param authCode 授权码
   * @return openid
   * @see WxPayService#authcode2Openid(WxPayAuthcode2OpenidRequest)
   */
  String authcode2Openid(String authCode) throws WxPayException;

  /**
   * <pre>
   * 获取仿真测试系统的验签密钥.
   * 请求Url： https://api.mch.weixin.qq.com/sandboxnew/pay/getsignkey
   * 是否需要证书： 否
   * 请求方式： POST
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_1
   * </pre>
   */
  String getSandboxSignKey() throws WxPayException;

  /**
   * <pre>
   * 发放代金券
   * 接口请求链接：https://api.mch.weixin.qq.com/mmpaymkttransfers/send_coupon
   * 是否需要证书：请求需要双向证书。
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_3
   * </pre>
   */
  WxPayCouponSendResult sendCoupon(WxPayCouponSendRequest request) throws WxPayException;

  /**
   * <pre>
   * 查询代金券批次.
   * 接口请求链接：https://api.mch.weixin.qq.com/mmpaymkttransfers/query_coupon_stock
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_4
   * </pre>
   */
  WxPayCouponStockQueryResult queryCouponStock(WxPayCouponStockQueryRequest request) throws WxPayException;

  /**
   * <pre>
   * 查询代金券信息.
   * 接口请求链接：https://api.mch.weixin.qq.com/mmpaymkttransfers/querycouponsinfo
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_5
   * </pre>
   */
  WxPayCouponInfoQueryResult queryCouponInfo(WxPayCouponInfoQueryRequest request) throws WxPayException;

  /**
   * 获取微信请求数据，方便接口调用方获取处理.
   */
  WxPayApiData getWxApiData();

  /**
   * <pre>
   * 拉取订单评价数据.
   * 商户可以通过该接口拉取用户在微信支付交易记录中针对你的支付记录进行的评价内容。商户可结合商户系统逻辑对该内容数据进行存储、分析、展示、客服回访以及其他使用。如商户业务对评价内容有依赖，可主动引导用户进入微信支付交易记录进行评价。
   * 注意：
   * 1. 该内容所有权为提供内容的微信用户，商户在使用内容的过程中应遵从用户意愿
   * 2. 一次最多拉取200条评价数据，可根据时间区间分批次拉取
   * 3. 接口只能拉取最近三个月以内的评价数据
   * 接口链接：https://api.mch.weixin.qq.com/billcommentsp/batchquerycomment
   * 是否需要证书：需要
   * 文档地址：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_17&index=10
   * </pre>
   *
   * @param beginDate 开始时间
   * @param endDate   结束时间
   * @param offset    位移
   * @param limit     条数
   */
  String queryComment(Date beginDate, Date endDate, Integer offset, Integer limit) throws WxPayException;
}
