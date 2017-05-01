import java.util.LinkedList;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * The Class RandomTrader.
 */

/**
 * @author jon
 *
 */
public class RandomTrader extends Trader {
	
	private double buyRate, sellRate;
	Random rand  = new Random();
	
	
	
	/**
	 * The Enum Mode.
	 */
	public enum Mode
	{
		BALANCED, AGGRESSIVE_BUY, AGGRESSIVE_SELL
	}
	
	private Mode[] ranMode = Mode.values();
	
	private Mode mode;
	
	/**
	 * Instantiates a new random trader.
	 *
	 * @param mode the mode
	 * @param i the i
	 */
	RandomTrader(int i)
	{
		setMode(Mode.BALANCED);
		setTraderName("RanTrader " + String.valueOf(i));
	}
	
	/**
	 * Gets the buy rate.
	 *
	 * @return the buy rate
	 */
	public double getBuyRate()
	{
		return buyRate;
	}
	
	/**
	 * Gets the sell rate.
	 *
	 * @return the sell rate
	 */
	public double getSellRate()
	{
		return sellRate;
	}
	
	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	public void setMode(Mode mode)
	{
		this.mode = mode;
		switch (mode)
		{
			case BALANCED:
					buyRate = 0.01;
					sellRate = 0.01;
					break;
			case AGGRESSIVE_BUY:
					buyRate = 0.02;
					sellRate = 0.005;
					break;
			case AGGRESSIVE_SELL:
					buyRate = 0.005;
					sellRate = 0.02;
					break;
			default: 
		}
	}
	
	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public Mode getMode()
	{
		return mode;
	}
	
	/**
	 * Switch mode.
	 *
	 * @param ranNum the ran num
	 */
	public void switchMode(double ranNum)
	{
		switch (getMode())
		{
			case BALANCED:
					if(ranNum < 0.1)
						setMode(Mode.AGGRESSIVE_SELL);
					if(ranNum > 0.1 && ranNum < 0.2)
						setMode(Mode.AGGRESSIVE_BUY);
					//System.out.println("blaaaaaaaaaaaaaa " + ranNum);
					break;
			case AGGRESSIVE_BUY:
					if(ranNum < 0.7)
						setMode(Mode.BALANCED);
					break;
			case AGGRESSIVE_SELL:
					if(ranNum < 0.6)
						setMode(Mode.BALANCED);
					break;
			default: 
		}
	}
	
	/**
	 * New order.
	 *
	 * @param client the client
	 * @param company the company
	 * @return the double
	 */
	public double newOrder(Client client, Company company)
	{
		int quantity = randomQuantity();
		//System.out.println(quantity);
		boolean orderType = true;
		if(client.hasShare(company))
		{
			for(Order o : getOrderList())
			{
				if(o.getCompanyName().equals(company.getName()))
					orderType = o.getOrderType();
				else
					orderType = company.randomBool();
			}
		}
		else
			orderType = true;
		
		if(orderType == false)
		{
			quantity = -quantity;
			company.setSellCount(quantity);
		}
		else
		{
			company.setBuyCount(quantity);
		}
			
		if(company.getName().equals("Pear Computing"))
			System.out.println("Qusn" + quantity);
		Order order = new Order(company,quantity,orderType,quantity*company.getCurrentShareValue(),"RiskLev",client);
		if(orderType == true)
			getOrderList().addFirst(order);
		else
			getOrderList().addLast(order);
		return quantity*company.getCurrentShareValue();
	}
	
	/**
	 * Complete order.
	 *
	 * @param o the o
	 */
	public void completeOrder(Order o)
	{	
		for(Client c: getClients())
		{
			if(o.getClientName().equals(c.getName()))
			{
				if(o.getOrderType() == true)
				{
					if(o.getCompany().getSellCount() != 0)
					{
						c.updateCash(-(o.getQuantity()*o.getCurrentShareValue()));
						if((o.getQuantity()/o.getCompany().getFinalBuyCount())*Math.abs(o.getCompany().getFinalSellCount()) >= o.getQuantity())
						{
							c.newShare(o.getQuantity(), o.getCompany());
							//System.out.println("worked?" + o.getCompany().setBuyCount);
							//o.getCompany().setBuyCount(-(o.getQuantity()));
							o.getCompany().setSellCount(o.getQuantity());
							o.isFullyCompleted();
							c.calculateNetWorth();
							break;
						}
						else
						{
							c.newShare(Math.floor((o.getQuantity()/o.getCompany().getFinalBuyCount())*Math.abs(o.getCompany().getFinalSellCount())), o.getCompany());
							if(o.getCompany().getName().equals("Pear Computing"))
							{
								System.out.println("Buy ratio got " + ( Math.floor((o.getQuantity()/o.getCompany().getFinalBuyCount())*Math.abs(o.getCompany().getFinalSellCount()))));
								System.out.println(o.getQuantity() + " final buy = " + o.getCompany().getFinalBuyCount());
							}
								
							//o.getCompany().setBuyCount(( Math.floor(-(o.getQuantity()/o.getCompany().getFinalBuyCount())*Math.abs(o.getCompany().getFinalSellCount()))));
							o.getCompany().setSellCount(( Math.ceil((o.getQuantity()/o.getCompany().getFinalBuyCount())*Math.abs(o.getCompany().getFinalSellCount()))));
							c.calculateNetWorth();
							break;
						}
						
					}
				}
				else
				{
					if(o.getCompany().getBuyCount() != 0)
					{
						c.updateCash(-(o.getQuantity()*o.getCurrentShareValue()));
						if((o.getQuantity()/o.getCompany().getFinalSellCount())*o.getCompany().getFinalBuyCount() <= o.getQuantity())
						{
							c.newShare(o.getQuantity(), o.getCompany());
							//o.getCompany().setSellCount(-(o.getQuantity()));
							o.getCompany().setBuyCount(-o.getQuantity());
							o.isFullyCompleted();
							c.calculateNetWorth();
							break;
						}
						else
						{
							if(o.getCompany().getFinalBuyCount() > Math.abs(o.getCompany().getFinalSellCount()))
							{
								c.newShare(o.getQuantity(), o.getCompany());
								o.getCompany().setBuyCount(o.getQuantity());
							}
							else
							{
										c.newShare(Math.ceil(-((o.getQuantity()/o.getCompany().getFinalSellCount())*o.getCompany().getFinalBuyCount())), o.getCompany());
										//o.getCompany().setSellCount((Math.ceil(((o.getQuantity()/o.getCompany().getFinalSellCount())*o.getCompany().getFinalBuyCount()))));
										o.getCompany().setBuyCount(-(Math.floor(((o.getQuantity()/o.getCompany().getFinalSellCount())*o.getCompany().getFinalBuyCount()))));
							}
							c.calculateNetWorth();
							break;
						}
					}
				}
					
			}
		}
	}
	
	/**
	 * Random quantity.
	 *
	 * @return the int
	 */
	public int randomQuantity()
	{
		LinkedList temp = new LinkedList();
		for(int i = 0; i<6; i++)
		{
			if(i == 0) 
				temp.add(50);
			else
				temp.add(100*i);
		}
		return (int) temp.get(rand.nextInt(6));
	}

}
