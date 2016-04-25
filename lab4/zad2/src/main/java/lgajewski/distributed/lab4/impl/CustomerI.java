package lgajewski.distributed.lab4.impl;


import Ice.Current;
import demo.*;

import java.util.ArrayList;
import java.util.List;

public class CustomerI extends _CustomerDisp {

    private long id;
    private Name name;

    private boolean logged = false;

    private List<Loan> loans = new ArrayList<>();
    private List<Investment> investments = new ArrayList<>();

    public CustomerI(long id, Name name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public long getUniqueId(Current __current) {
        return id;
    }

    @Override
    public Name getName(Current __current) {
        return name;
    }

    @Override
    public void login(Current __current) {
        logged = true;
    }

    @Override
    public void logout(Current __current) {
        logged = false;
    }

    @Override
    public void addLoan(Loan loan, Current __current) throws AuthorizationException {
        checkAuthorization();
        loans.add(loan);
    }

    @Override
    public void addInvestment(Investment investment, Current __current) throws AuthorizationException {
        checkAuthorization();
        investments.add(investment);
    }

    @Override
    public int calculateLoan(int period, Current __current) throws AuthorizationException {
        checkAuthorization();

        float basicLoan = 1000;

        float rate = investments.size() / loans.size();

        return (int) (basicLoan * rate);
    }

    @Override
    public int calculateInvestment(int period, int amount, Current __current) throws AuthorizationException {
        checkAuthorization();
        float interest = 0.1f + (period / 10) * 0.1f;

        return (int) (amount * (1 + interest));
    }

    @Override
    public Loan[] getLoans(Current __current) throws AuthorizationException {
        checkAuthorization();

        Loan[] loans = new Loan[this.loans.size()];
        this.loans.toArray(loans);

        return loans;
    }

    @Override
    public Investment[] getInvestments(Current __current) throws AuthorizationException {
        checkAuthorization();

        Investment[] investments = new Investment[this.investments.size()];
        this.investments.toArray(investments);

        return investments;
    }

    private void checkAuthorization() throws AuthorizationException {
        if (!logged) {
            throw new AuthorizationException();
        }
    }
}
