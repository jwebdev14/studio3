/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.io.IBaseRemoteConnectionPoint;
import com.aptana.core.io.IConnectionPoint;
import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.syncing.core.ISiteConnection;
import com.aptana.syncing.core.SiteConnectionUtils;
import com.aptana.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ui.ftp.internal.FTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployWizardPage extends WizardPage implements FTPConnectionPropertyComposite.Listener
{

	public static final String NAME = "FTPDeployment"; //$NON-NLS-1$
	private static final String ICON_PATH = "icons/ftp.png"; //$NON-NLS-1$

	private IProject project;
	private FTPDeployComposite ftpConnectionComposite;
	private IBaseRemoteConnectionPoint connectionPoint;

	protected FTPDeployWizardPage(IProject project)
	{
		super(NAME, Messages.FTPDeployWizardPage_Title, Activator.getImageDescriptor(ICON_PATH));
		this.project = project;
		// checks if the project already has an associated FTP connection and fills the info automatically if one exists
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(project, true);
		String lastConnection = DeployPreferenceUtil.getDeployEndpoint(project);
		IConnectionPoint connection;
		for (ISiteConnection site : sites)
		{
			connection = site.getDestination();
			if ((connection != null && connection.getName().equals(lastConnection))
					|| (lastConnection == null && connection instanceof IBaseRemoteConnectionPoint))
			{
				connectionPoint = (IBaseRemoteConnectionPoint) connection;
				break;
			}
		}
	}

	public IBaseRemoteConnectionPoint getConnectionPoint()
	{
		return ftpConnectionComposite.getConnectionPoint();
	}

	public boolean isAutoSyncSelected()
	{
		return ftpConnectionComposite.isAutoSyncSelected();
	}

	public SyncDirection getSyncDirection()
	{
		return ftpConnectionComposite.getSyncDirection();
	}

	public boolean completePage()
	{
		boolean complete = ftpConnectionComposite.completeConnection();
		// persists the auto-sync setting
		boolean autoSync = isAutoSyncSelected();
		SyncPreferenceUtil.setAutoSync(project, autoSync);
		if (autoSync)
		{
			SyncPreferenceUtil.setAutoSyncDirection(project, getSyncDirection());
		}

		return complete;
	}

	public void createControl(Composite parent)
	{
		ftpConnectionComposite = new FTPDeployComposite(parent, SWT.NONE, connectionPoint, this);
		ftpConnectionComposite
				.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(ftpConnectionComposite);

		initializeDialogUnits(parent);
		Dialog.applyDialogFont(ftpConnectionComposite);

		boolean autoSync = SyncPreferenceUtil.isAutoSync(project);
		ftpConnectionComposite.setAutoSyncSelected(autoSync);
		if (autoSync)
		{
			ftpConnectionComposite.setSyncDirection(SyncPreferenceUtil.getAutoSyncDirection(project));
		}
		ftpConnectionComposite.validate();
	}

	@Override
	public IWizardPage getNextPage()
	{
		return null;
	}

	public boolean close()
	{
		return false;
	}

	public void error(String message)
	{
		if (message == null)
		{
			setErrorMessage(null);
			setMessage(null);
		}
		else
		{
			setErrorMessage(message);
		}
		setPageComplete(message == null);
	}

	public void layoutShell()
	{
	}

	public void lockUI(boolean lock)
	{
	}

	public void setValid(boolean valid)
	{
	}
}
