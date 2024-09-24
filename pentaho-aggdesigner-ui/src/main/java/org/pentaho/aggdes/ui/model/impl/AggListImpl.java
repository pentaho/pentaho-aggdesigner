/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU General Public License, version 2 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
*
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.ui.model.impl;

import static org.pentaho.aggdes.ui.model.AggListEvent.Type.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.AggListEvent;
import org.pentaho.aggdes.ui.model.AggListListener;
import org.pentaho.aggdes.ui.util.EventSupport;

/**
 * Thin wrapper around a list adding the ability to fire change events.
 * 
 * @author mlowery
 */
public class AggListImpl implements AggList {

  private static final Log logger = LogFactory.getLog(AggListImpl.class);

  private List<UIAggregate> list;

  private EventSupport<AggListListener> eventSupport = new EventSupport<AggListListener>();

  private int selectedIndex = -1;
  
  public AggListImpl() {
    this(new ArrayList<UIAggregate>());
  }

  public AggListImpl(List<UIAggregate> list) {
    super();
    this.list = Collections.synchronizedList(list);
  }

  public void addAgg(UIAggregate agg) {
    boolean added = list.add(agg);
    if (added) {
      fireEvent(new AggListEvent(this, AGG_ADDED, list.size() - 1));
    }
  }

  protected void fireEvent(AggListEvent e) {
    for (AggListListener l : eventSupport.getListeners()) {
      l.listChanged(e);
    }
  }

  public void addAggListListener(AggListListener l) {
    eventSupport.addListener(l);
  }

  public UIAggregate getAgg(int index) {
    if(index < 0 || index > list.size()){
      return null;
    } else {
      return list.get(index);
    }
  }

  public int getSize() {
    return list.size();
  }

  public void removeAggListListener(AggListListener l) {
    eventSupport.removeListener(l);
  }

  public void removeAgg(int index) {
    if (0 <= index && index < list.size()) {
        UIAggregate old = list.remove(index);
      if(index == selectedIndex){
        this.setSelectedIndex(-1);
      }
      if (old != null) {
        fireEvent(new AggListEvent(this, AGG_REMOVED, index));
      }
    } else {
      if (logger.isWarnEnabled()) {
        logger.warn("index out of bounds: " + index);
      }
    }
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void setSelectedIndex(int index) {
    if(selectedIndex == index){
      return;
    }
    fireEvent(new AggListEvent(this, SELECTION_CHANGING, selectedIndex));
    selectedIndex = index;
    fireEvent(new AggListEvent(this, SELECTION_CHANGED, selectedIndex));
  }

  public UIAggregate getSelectedValue() {
    return (selectedIndex > -1)? list.get(selectedIndex) : null;
  }

  public void aggChanged(UIAggregate agg) {
    if( ! list.contains(agg)){
      throw new IllegalArgumentException("Aggregate not in list");
    }
    
    fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg)));
  }
  
  public Iterator<UIAggregate> iterator() {
    return Collections.unmodifiableList(list).iterator();
  }

  public void addAggs(List<UIAggregate> aggs) {
    boolean added = list.addAll(aggs);
    if (added) {
      fireEvent(new AggListEvent(this, AGGS_ADDED, list.size() - aggs.size()));
    }
  }

  public void clearAggs() {
    list.clear();
    setSelectedIndex(-1);
    fireEvent(new AggListEvent(this, AGGS_CLEARED, 0));
  }

  public void moveAggDown(UIAggregate agg) {
    int curpos = list.indexOf(agg);
    if(curpos+1 == list.size()){
      //already at end.
      return;
    }

    //remove current selection
    //setSelectedIndex(-1);
    
    list.remove(agg);
    list.add(curpos+1, agg);
    fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg)));
    fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg) -1));
    setSelectedIndex(curpos+1);
  }

  public void moveAggUp(UIAggregate agg) {
    int curpos = list.indexOf(agg);
    if(curpos == 0){
      //already at start.
      return;
    }
    
    //remove current selection
    //setSelectedIndex(-1);
    
    list.remove(agg);
    list.add(curpos-1, agg);
    fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg)));
    fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg) +1 ));
    setSelectedIndex(curpos-1);
  }

  public void uncheckAll() {
    for(UIAggregate agg : list){
      agg.setEnabled(false);
      fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg)));
    }
  }
  
  public void checkAll() {
    for(UIAggregate agg : list){
      agg.setEnabled(true);
      fireEvent(new AggListEvent(this, AGG_CHANGED, list.indexOf(agg)));
    }
    
  }
}
