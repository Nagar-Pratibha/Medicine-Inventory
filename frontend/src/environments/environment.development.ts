const host = 'http://localhost:8099';

export const environment = {
    production: false,
    endpoints: {
        
        fetchAllOrdersAdmin: `${host}/api/admin/orders`,
        userBaseEndpoint: `${host}/api/users`,
        medicineBaseEndpoint: `${host}/api/medicines`
    }
};
