package com.rn.tool.gcalimporter.helper;

import lombok.Getter;

/**
 * Class of simple counter
 */
public class Counter {

  /**
   * Initial value
   */
  @Getter
  private long i = 0;

  /**
   * Constructor
   *
   * @param initialValue initial value
   */
  private Counter(long initialValue) {
    this.i = initialValue;
  }

  /**
   * Factory method
   *
   * @param initialValue initial value
   * @return instance of {@link Counter}
   */
  public static Counter create(long initialValue) {
    return new Counter(initialValue);
  }

  /**
   * Method for increment
   *
   * @return incremented value
   */
  public long increment() {
    return this.i++;
  }

  /**
   * Method for decrement
   *
   * @return decremented value
   */
  public long decrement() {
    return this.i--;
  }
}
