package com.hqsolution.hqserver.app.core.participant;

import java.io.Serializable;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import com.hqsolution.hqserver.app.common.DatabaseConnection;
import com.hqsolution.hqserver.app.dao.DbDao;
import com.hqsolution.hqserver.app.dao.factory.DbDaoFactory;
import com.hqsolution.hqserver.app.dao.idao.IAccount;
import com.hqsolution.hqserver.app.dto.HQAccount;
import com.hqsolution.hqserver.util.MessageHelper;
import com.hqsolution.hqserver.util.SystemConstant;

/**
 * Check All Information about card include existing card, expire card and activated card
 * @author HUNGPT
 *
 */
public class CheckAccount implements TransactionParticipant {

	@Override
	public void abort(long id, Serializable serializeable) {
		System.out.println(this.getClass().getName() + " abort");
	}

	@Override
	public void commit(long id, Serializable serialieable) {
		System.out.println(this.getClass().getName() + " commit");
	}

	@Override
	public int prepare(long id, Serializable serializeable) {
		
		System.out.println(this.getClass().getName() + " prepare");
		/** get context from space **/
		Context ctx = (Context)serializeable;
		
		/** get message from context **/
		ISOMsg msg = (ISOMsg)ctx.get(SystemConstant.REQUEST);
		msg.dump(System.out, "");
		try {
			System.out.println(ISOUtil.hexString(msg.pack()));
		} catch (ISOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/** Get connection from context **/
		DatabaseConnection con = (DatabaseConnection)ctx.get(SystemConstant.CONNECTION);
		if(con == null){
			System.out.println("@@@@@@@@@@DatabaseConnection@@@@@@@@@" + con == null);
			ctx.put(SystemConstant.RC, "12");
			return ABORTED | READONLY | NO_JOIN;
		}
		
		HQAccount account = MessageHelper.getHQAccount(msg);
		if(account == null){
			System.out.println("Account is null");
		}
		DbDao dao = DbDaoFactory.getInstances();
		IAccount accountDao = dao.getAccount();
		int checkResult = accountDao.checkAccount(con, account);
		System.out.println("@@@@@@@@@@checkResult@@@@@@@@@" + checkResult);
		if(checkResult > 0 ){
			ctx.put(SystemConstant.RC, "14");
			return ABORTED | READONLY | NO_JOIN;
		}
		
		return PREPARED | NO_JOIN ;
	}

}
