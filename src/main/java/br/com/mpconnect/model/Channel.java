package br.com.mpconnect.model;

public enum Channel 
{
	ML{
		public Origem getOrigem(){return new Origem(1L);};
	},
	B2W
	{
		public Origem getOrigem(){return new Origem(3L);};
	};

	public abstract Origem getOrigem();
}
