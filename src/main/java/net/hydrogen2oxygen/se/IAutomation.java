package net.hydrogen2oxygen.se;

import net.hydrogen2oxygen.se.exceptions.CleanUpException;
import net.hydrogen2oxygen.se.exceptions.PreconditionsException;
import net.hydrogen2oxygen.se.protocol.Protocol;

/**
 * Every selenium test, automation or snippet implements this interface
 */
public interface IAutomation {

    /**
     * This works also as a prepare method, because it runs before everything else
     *
     * @throws PreconditionsException thrown if the preconditions don't exist
     */
    void checkPreconditions() throws PreconditionsException;

    /**
     * Clean up the mess afterwards
     *
     * @throws CleanUpException thrown usually when unexpected errors occurs
     */
    void cleanUp() throws CleanUpException;

    /**
     * Main run method of the automation
     *
     * @throws Exception
     * @throws Exception thrown usually when unexpected errors occurs
     */
    void run() throws Exception;

    /**
     * Set Se(lenium) (useful for parallel automation inside a group)
     *
     * @param se {@link Se} instance to use
     */
    void setSe(Se se);

    Protocol getProtocol();
}
