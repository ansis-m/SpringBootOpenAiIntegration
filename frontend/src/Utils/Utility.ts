export class Utility {

    private static baseUrl = "http://localhost:8081";
    private static terminateUrl = new URL(this.baseUrl + "/terminate");
    private static saveUrl = new URL(this.baseUrl + "/save");

    static saveSession = async (name: string) => {
        if (name) {
            try{
                const response = await fetch(this.saveUrl, {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: name
                })
                if (!response.ok) {
                    window.alert("something wrong with saveSession");
                }
            } catch (e) {
                window.alert(e)
            }

        }
    };
}