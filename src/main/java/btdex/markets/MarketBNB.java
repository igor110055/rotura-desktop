package btdex.markets;

import java.util.HashMap;

import com.binance.dex.api.client.encoding.Crypto;

import btdex.core.Globals;
import btdex.locale.Translation;

public class MarketBNB extends MarketCrypto {
	
	public String getTicker() {
		return "BNB";
	}
	
	@Override
	public String getChainDetails() {
		return "Binance Chain";
	}
	
	@Override
	public String getExplorerLink() {
		return "https://explorer.binance.org/";
	}
	
	@Override
	public long getID() {
		return MARKET_BNB;
	}
	
	@Override
	public int getUCA_ID() {
		return 1839;
	}
	
	@Override
	public void validate(HashMap<String, String> fields) throws Exception {
		super.validate(fields);
		
		String addr = fields.get(ADDRESS);
		
		if(!addr.startsWith(Globals.getInstance().isTestnet() ? "tbnb1" : "bnb1")) {
			throw new Exception(Translation.tr("mkt_invalid_address", addr, toString()));
		}
		try {
			Crypto.decodeAddress(addr);
		}
		catch (Exception e) {
			throw new Exception(Translation.tr("mkt_invalid_address", addr, toString()));
		}
	}
}
