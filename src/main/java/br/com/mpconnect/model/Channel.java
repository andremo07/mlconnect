package br.com.mpconnect.model;

public enum Channel 
{
	ML{
		public Origem getOrigem(){return new Origem(1L,"ML");};
	},
	B2W
	{
		public Origem getOrigem(){return new Origem(3L,"B2W");};
	},
	AMERICANAS
	{
		public Origem getOrigem(){return new Origem(4L,"Lojas Americanas");};
	},
	SUBMARINO
	{
		public Origem getOrigem(){return new Origem(5L,"Submarino");};
	},
	SHOPTIME
	{
		public Origem getOrigem(){return new Origem(6L,"Shoptime");};
	};

	public abstract Origem getOrigem();
		
	public static Channel lookup(String channelName)
	{
		for(Channel channel : Channel.values())
			if(channel.getOrigem().getNome().equals(channelName))
				return channel;
		return Channel.ML;
	}
}
