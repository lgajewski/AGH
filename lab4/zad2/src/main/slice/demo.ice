#ifndef SR_DEMO_ICE
#define SR_DEMO_ICE

//#include <Ice/BuiltinSequences.ice>

module demo
{

    exception AuthorizationException {};

    enum currency {PLN, USD, EUR, CHF};

	struct Name
	{
		string firstName;
		string lastName;
	};

	struct Loan
	{
	    int period;
	    int amount;
	    float interestRate;
	};

	struct Investment
    {
        int period;
        int amount;
        int interest;
    };

    sequence<Loan> LoanSequence;
    sequence<Investment> InvestmentSequence;
	
	interface Customer
	{
		long getUniqueId();
		Name getName();

		void login();
		void logout();

		void addLoan(Loan loan) throws AuthorizationException;
		void addInvestment(Investment investment) throws AuthorizationException;

		int calculateLoan(int period) throws AuthorizationException;
		int calculateInvestment(int period, int amount) throws AuthorizationException;
        LoanSequence getLoans() throws AuthorizationException;
		InvestmentSequence getInvestments() throws AuthorizationException;
	};


};

#endif
