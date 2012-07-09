package tray.linux;

interface TrayMethodsThatAreInvokedByTheNativeCounterpart {

	void fireActionActivated();

	void fireMenuAction(int menuItemIndex);
}
