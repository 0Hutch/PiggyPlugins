package com.wigglydonplugins.AutoVardorvis.state;

import com.wigglydonplugins.AutoVardorvis.AutoVardorvisPlugin.MainClassContext;
import com.wigglydonplugins.AutoVardorvis.state.botStates.BankingState;
import com.wigglydonplugins.AutoVardorvis.state.botStates.FightingState;
import com.wigglydonplugins.AutoVardorvis.state.botStates.GoToBankState;
import com.wigglydonplugins.AutoVardorvis.state.botStates.GoToVardorvisState;
import com.wigglydonplugins.AutoVardorvis.state.botStates.TestingState;

public class StateHandler {

  public enum State {
    TESTING,
    BANKING,
    GO_TO_VARDORVIS,
    FIGHTING,
    GO_TO_BANK,
  }

  public void handleState(State state, MainClassContext context) {
    switch (state) {
      case TESTING:
        new TestingState().execute(context);
        break;
      case BANKING:
        new BankingState().execute(context);
        break;
      case FIGHTING:
        new FightingState().execute(context);
        break;
      case GO_TO_VARDORVIS:
        new GoToVardorvisState().execute(context);
        break;
      case GO_TO_BANK:
        new GoToBankState().execute(context);
        break;
      default:
        System.out.println("Unknown state!");
    }
  }
}
