package datacite.oai.provider.util;

/*******************************************************************************
* Copyright (c) 2011 DataCite
*
* All rights reserved. This program and the accompanying 
* materials are made available under the terms of the 
* Apache License, Version 2.0 which accompanies 
* this distribution, and is available at 
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/


/**
 * Encapsulates a simple pair of values
 */
public final class Pair< FIRST, SECOND > implements Comparable< Pair< FIRST, SECOND > >
{
    /*********************************************************************************************
     * VARIABLE DECLARATIONS                                                                     *
     *********************************************************************************************/

    // The first value in the pair
    private FIRST _first;

    // The second value in the pair
    private SECOND _second;

    /*********************************************************************************************
     * CONSTRUCTORS AND PUBLIC INITIALISATION                                                    *
     *********************************************************************************************/

    public Pair()
    {
    }

    public Pair( FIRST first, SECOND second )
    {
        setFirst( first );
        setSecond( second );
    }

    /*********************************************************************************************
     * ACCESSOR METHODS                                                                          *
     *********************************************************************************************/

    public FIRST getFirst()
    {
        return _first;
    }

    public void setFirst( FIRST first )
    {
        _first = first;
    }

    public SECOND getSecond()
    {
        return _second;
    }

    public void setSecond( SECOND second )
    {
        _second = second;
    }

    /*********************************************************************************************
     * PUBLIC METHODS                                                                            *
     *********************************************************************************************/

    public int compareTo( Pair< FIRST, SECOND > other )
    {
        int result = 0;
        if( other == null )
        {
            result = 1;
        }

        // Compare FIRST
        if( result == 0 )
        {
            result = compare( _first, other._first );
        }

        // Compare SECOND
        if( result == 0 )
        {
            result = compare( _second, other._second );
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append( "Pair( " );
        builder.append( "first = \"" ).append( _first ).append( "\"" );
        builder.append( ", " );
        builder.append( "second = \"" ).append( _second ).append( "\"" );
        builder.append( " )" );

        return builder.toString();
    }

    /*********************************************************************************************
     * PRIVATE METHODS                                                                           *
     *********************************************************************************************/

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public int compare( Object o1, Object o2 )
    {
        if( o1 != null && o2 != null )
        {
            if( o1 instanceof Comparable )
            {
                return ((Comparable)o1).compareTo( o2 );
            }
            else if( o2 instanceof Comparable )
            {
                return -((Comparable)o2).compareTo( o1 );
            }
            else
            {
                return 0;
            }
        }
        else if( o1 != null )
        {
            return 1;
        }
        else if( o2 != null )
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
