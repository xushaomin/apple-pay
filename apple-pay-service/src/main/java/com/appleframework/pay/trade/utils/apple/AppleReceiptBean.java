package com.appleframework.pay.trade.utils.apple;

import java.io.Serializable;
import java.util.List;

public class AppleReceiptBean implements Serializable {

	private static final long serialVersionUID = -3566773423244003397L;

	private String receipt_type;
	private Long adam_id;
	private Integer app_item_id;
	private String bundle_id;
	private String application_version;
	private Long download_id;
	private Long version_external_identifier;
	private String receipt_creation_date;
	private String receipt_creation_date_ms;
	private String receipt_creation_date_pst;
	private String request_date;
	private String request_date_ms;
	private String request_date_pst;
	private String original_purchase_date;
	private String original_purchase_date_ms;
	private String original_purchase_date_pst;
	private String original_application_version;
	private List<AppleInAppBean> in_app;

	public String getReceipt_type() {
		return receipt_type;
	}

	public void setReceipt_type(String receipt_type) {
		this.receipt_type = receipt_type;
	}

	public Long getAdam_id() {
		return adam_id;
	}

	public void setAdam_id(Long adam_id) {
		this.adam_id = adam_id;
	}

	public Integer getApp_item_id() {
		return app_item_id;
	}

	public void setApp_item_id(Integer app_item_id) {
		this.app_item_id = app_item_id;
	}

	public String getBundle_id() {
		return bundle_id;
	}

	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public String getApplication_version() {
		return application_version;
	}

	public void setApplication_version(String application_version) {
		this.application_version = application_version;
	}

	public Long getDownload_id() {
		return download_id;
	}

	public void setDownload_id(Long download_id) {
		this.download_id = download_id;
	}

	public Long getVersion_external_identifier() {
		return version_external_identifier;
	}

	public void setVersion_external_identifier(Long version_external_identifier) {
		this.version_external_identifier = version_external_identifier;
	}

	public String getReceipt_creation_date() {
		return receipt_creation_date;
	}

	public void setReceipt_creation_date(String receipt_creation_date) {
		this.receipt_creation_date = receipt_creation_date;
	}

	public String getReceipt_creation_date_ms() {
		return receipt_creation_date_ms;
	}

	public void setReceipt_creation_date_ms(String receipt_creation_date_ms) {
		this.receipt_creation_date_ms = receipt_creation_date_ms;
	}

	public String getReceipt_creation_date_pst() {
		return receipt_creation_date_pst;
	}

	public void setReceipt_creation_date_pst(String receipt_creation_date_pst) {
		this.receipt_creation_date_pst = receipt_creation_date_pst;
	}

	public String getRequest_date() {
		return request_date;
	}

	public void setRequest_date(String request_date) {
		this.request_date = request_date;
	}

	public String getRequest_date_ms() {
		return request_date_ms;
	}

	public void setRequest_date_ms(String request_date_ms) {
		this.request_date_ms = request_date_ms;
	}

	public String getRequest_date_pst() {
		return request_date_pst;
	}

	public void setRequest_date_pst(String request_date_pst) {
		this.request_date_pst = request_date_pst;
	}

	public String getOriginal_purchase_date() {
		return original_purchase_date;
	}

	public void setOriginal_purchase_date(String original_purchase_date) {
		this.original_purchase_date = original_purchase_date;
	}

	public String getOriginal_purchase_date_ms() {
		return original_purchase_date_ms;
	}

	public void setOriginal_purchase_date_ms(String original_purchase_date_ms) {
		this.original_purchase_date_ms = original_purchase_date_ms;
	}

	public String getOriginal_purchase_date_pst() {
		return original_purchase_date_pst;
	}

	public void setOriginal_purchase_date_pst(String original_purchase_date_pst) {
		this.original_purchase_date_pst = original_purchase_date_pst;
	}

	public String getOriginal_application_version() {
		return original_application_version;
	}

	public void setOriginal_application_version(String original_application_version) {
		this.original_application_version = original_application_version;
	}

	public List<AppleInAppBean> getIn_app() {
		return in_app;
	}

	public void setIn_app(List<AppleInAppBean> in_app) {
		this.in_app = in_app;
	}

}
