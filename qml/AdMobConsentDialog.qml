import QtQuick 2.12
import QtQuick.Controls 2.5
import QtQuick.Layouts 1.12
import QtQuick.Dialogs 1.2

import "Util.js" as UtilScript

Dialog {
    id: adMobConsentDialog

    signal personalizedAdsSelected()
    signal nonPersonalizedAdsSelected()

    contentItem: Rectangle {
        implicitWidth:  UtilScript.pt(300)
        implicitHeight: UtilScript.pt(300)
        color:          "white"
        border.width:   UtilScript.pt(1)
        border.color:   "#eb6dc0"

        ColumnLayout {
            anchors.fill:         parent
            anchors.topMargin:    UtilScript.pt(16)
            anchors.bottomMargin: UtilScript.pt(16)
            spacing:              UtilScript.pt(16)

            Text {
                leftPadding:         UtilScript.pt(16)
                rightPadding:        UtilScript.pt(16)
                text:                qsTr("We keep this app free by showing ads. Ad network will <a href=\"https://policies.google.com/technologies/ads\">collect data and use a unique identifier on your device</a> to show you ads. <b>Do you allow to use your data to tailor ads for you?</b>")
                color:               "black"
                font.pointSize:      16
                font.family:         "Helvetica"
                horizontalAlignment: Text.AlignJustify
                verticalAlignment:   Text.AlignVCenter
                wrapMode:            Text.Wrap
                fontSizeMode:        Text.Fit
                minimumPointSize:    8
                textFormat:          Text.StyledText
                Layout.fillWidth:    true
                Layout.fillHeight:   true

                onLinkActivated: {
                    Qt.openUrlExternally(link);
                }
            }

            Image {
                width:            UtilScript.pt(280)
                height:           UtilScript.pt(56)
                source:           "qrc:/resources/images/button.png"
                Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter

                Text {
                    anchors.fill:        parent
                    anchors.margins:     UtilScript.pt(8)
                    text:                qsTr("Yes, show me relevant ads")
                    color:               "white"
                    font.pointSize:      16
                    font.family:         "Helvetica"
                    font.bold:           true
                    horizontalAlignment: Text.AlignHCenter
                    verticalAlignment:   Text.AlignVCenter
                    wrapMode:            Text.NoWrap
                    fontSizeMode:        Text.Fit
                    minimumPointSize:    8
                }

                MouseArea {
                    anchors.fill: parent

                    onClicked: {
                        adMobConsentDialog.personalizedAdsSelected();
                        adMobConsentDialog.close();
                    }
                }
            }

            Image {
                width:            UtilScript.pt(280)
                height:           UtilScript.pt(56)
                source:           "qrc:/resources/images/button.png"
                Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter

                Text {
                    anchors.fill:        parent
                    anchors.margins:     UtilScript.pt(8)
                    text:                qsTr("No, show me ads that are less relevant")
                    color:               "white"
                    font.pointSize:      16
                    font.family:         "Helvetica"
                    font.bold:           true
                    horizontalAlignment: Text.AlignHCenter
                    verticalAlignment:   Text.AlignVCenter
                    wrapMode:            Text.NoWrap
                    fontSizeMode:        Text.Fit
                    minimumPointSize:    8
                }

                MouseArea {
                    anchors.fill: parent

                    onClicked: {
                        adMobConsentDialog.nonPersonalizedAdsSelected();
                        adMobConsentDialog.close();
                    }
                }
            }
        }
    }
}