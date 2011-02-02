/*
The contents of this file are subject to the Common Public Attribution License
Version 1.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.projity.com/license . The License is based on the Mozilla Public
License Version 1.1 but Sections 14 and 15 have been added to cover use of
software over a computer network and provide for limited attribution for the
Original Developer. In addition, Exhibit A has been modified to be consistent
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
specific language governing rights and limitations under the License. The
Original Code is OpenProj. The Original Developer is the Initial Developer and
is Projity, Inc. All portions of the code written by Projity are Copyright (c)
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the
Projity End-User License Agreeement (the Projity License), in which case the
provisions of the Projity License are applicable instead of those above. If you
wish to allow use of your version of this file only under the terms of the
Projity License and not to allow others to use your version of this file under
the CPAL, indicate your decision by deleting the provisions above and replace
them with the notice and other provisions required by the Projity  License. If
you do not delete the provisions above, a recipient may use your version of this
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices
in Exhibits A and B of the license at http://www.projity.com/license. You should
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj,
an open source solution from Projity. Attribution URL: http://www.projity.com
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined
in the CPAL as a work which combines Covered Code or portions thereof with code
not governed by the terms of the CPAL. However, in addition to the other notice
obligations, all copies of the Covered Code in Executable and Source Code form
distributed must, as a form of attribution of the original author, include on
each user interface screen the "OpenProj" logo visible to all users.  The
OpenProj logo should be located horizontally aligned with the menu bar and left
justified on the top left of the screen adjacent to the File menu.  The logo
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it
must direct them back to http://www.projity.com.
*/
package com.projity.pm.graphic.gantt;

import java.util.HashMap;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.projity.pm.graphic.frames.ApplicationStartupFactory;
import com.projity.pm.graphic.frames.MainFrame;
import com.projity.preference.ConfigurationFile;
import com.projity.strings.Messages;
import com.projity.util.Environment;

/**
 *
 */
public class Main {
	public static Log log = LogFactory.getLog(Main.class);
	public static void main(String[] args) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", Environment.getStandAlone()?"OpenProj":"Project-ON-Demand");
		System.setProperty("apple.laf.useScreenMenuBar","true");
		Locale.setDefault(ConfigurationFile.getLocale());
		HashMap opts=ApplicationStartupFactory.extractOpts(args);
		String osName=System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("linux")){
			String javaExec=ConfigurationFile.getRunProperty("JAVA_EXE");
			//check jvm
			String javaVersion=System.getProperty("java.version");
			if (Environment.compareJavaVersion(javaVersion,"1.5")<0){
				String message=Messages.getStringWithParam("Text.badJavaVersion", javaVersion);
				if (javaExec!=null&&javaExec.length()>0) message+="\n"+Messages.getStringWithParam("Text.javaExecutable", new Object[]{javaExec,"JAVA_EXE","$HOME/.openproj/run.conf"});
				if (!opts.containsKey("silentlyFail")) JOptionPane.showMessageDialog(null,message, Messages.getContextString("Title.ProjityError"),JOptionPane.ERROR_MESSAGE);
				System.exit(64);
			}
			String javaVendor=System.getProperty("java.vendor");
			if (javaVendor==null || !(javaVendor.startsWith("Sun")||javaVendor.startsWith("IBM"))){
				String message=Messages.getStringWithParam("Text.badJavaVendor", javaVendor);
				if (javaExec!=null&&javaExec.length()>0) message+="\n"+Messages.getStringWithParam("Text.javaExecutable", new Object[]{javaExec,"JAVA_EXE","$HOME/.openproj/run.conf"});
				if (!opts.containsKey("silentlyFail")) JOptionPane.showMessageDialog(null,message, Messages.getContextString("Title.ProjityError"),JOptionPane.ERROR_MESSAGE);
				System.exit(64);
			}
		}


		boolean newLook = false;
//		HashMap opts = ApplicationStartupFactory.extractOpts(args); // allow setting menu look on command line - primarily for testing or webstart args
		log.info(opts);
//		newLook = opts.get("menu") == null;

		Environment.setNewLook(newLook);
//		if (!Environment.isNewLaf()) {
//			try {
//				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			} catch (Exception e) {
//			}
//		}
		ApplicationStartupFactory startupFactory=new ApplicationStartupFactory(opts); //put before to initialize standalone flag
		MainFrame frame = new MainFrame(Messages.getContextString("Text.ApplicationTitle"), null, null);
		boolean doWelcome = true; // to do see if project param exists in args
		startupFactory.instanceFromNewSession(frame,doWelcome);
	}
}
